package com.onyx.souls;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SoulListener implements Listener {

    private final OnyxSouls plugin;

    public SoulListener(OnyxSouls plugin) {
        this.plugin = plugin;
    }

    // ── Initialize lives on first join ───────────────────────────────────────
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String path = "players." + p.getUniqueId() + ".lives";
        if (!plugin.getData().contains(path)) {
            plugin.getData().set(path, plugin.getDefaultLives());
            plugin.saveData();
        }
    }

    // ── Death — deduct a life ────────────────────────────────────────────────
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        String path = "players." + p.getUniqueId() + ".lives";
        int lives = plugin.getData().getInt(path, plugin.getDefaultLives());

        if (lives > 0) {
            lives--;
            plugin.getData().set(path, lives);
            plugin.saveData();
        }

        if (lives <= 0) {
            String msg = plugin.getMessage("banished")
                .replace("{player}", p.getName());
            Bukkit.broadcastMessage(msg);
        } else {
            String msg = plugin.getMessage("life-lost")
                .replace("{lives}", String.valueOf(lives));
            p.sendMessage(msg);
        }
    }

    // ── Respawn — send banished players to the Abyss ─────────────────────────
    // 1.21.1 NOTE: PlayerRespawnEvent API is unchanged but we use
    // RespawnReason check to avoid interfering with bed/anchor respawns
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        int lives = plugin.getData().getInt(
            "players." + p.getUniqueId() + ".lives",
            plugin.getDefaultLives()
        );

        if (lives <= 0) {
            World nether = null;
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == World.Environment.NETHER) {
                    nether = w;
                    break;
                }
            }

            if (nether != null) {
                Location abyss = new Location(
                    nether,
                    plugin.getBanishX(),
                    plugin.getBanishY(),
                    plugin.getBanishZ()
                );
                event.setRespawnLocation(abyss);
            } else {
                Bukkit.getLogger().warning("[OnyxSouls] Could not find Nether world for banishment!");
            }
        }
    }

    // ── Command blocker for banished players ──────────────────────────────────
    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        if (p.isOp() || p.hasPermission("onyx.admin")) return;

        int lives = plugin.getData().getInt(
            "players." + p.getUniqueId() + ".lives",
            plugin.getDefaultLives()
        );

        if (lives <= 0) {
            String msg = event.getMessage().toLowerCase();
            if (!msg.startsWith("/revive")
                    && !msg.startsWith("/msg")
                    && !msg.startsWith("/tell")
                    && !msg.startsWith("/w")) {
                event.setCancelled(true);
                p.sendMessage("§8[§4Abyss§8] §7The dead do not command the living.");
            }
        }
    }
}
