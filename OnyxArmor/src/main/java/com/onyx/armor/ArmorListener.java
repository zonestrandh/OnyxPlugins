package com.onyx.armor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ArmorListener implements Listener {

    private final OnyxArmor plugin;
    private final Map<UUID, String> activeSetCache = new HashMap<>();

    public ArmorListener(OnyxArmor plugin) {
        this.plugin = plugin;
        ItemUtils.init(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override public void run() {
                updatePlayerEffects(event.getPlayer());
            }
        }.runTaskLater(plugin, 2L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activeSetCache.remove(event.getPlayer().getUniqueId());
    }

    // ── 1.21.1 FIX: InventoryView is now an interface, not a class ───────────
    // We no longer reference InventoryView directly — this was the root cause
    // of the IncompatibleClassChangeError crash on 1.20.1 Paper and was the
    // reason VentureChat also crashed. We avoid it entirely by using
    // getSlotType() and getRawSlot() on the event directly.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;

        boolean armorSlotInvolved =
            event.getSlotType() == InventoryType.SlotType.ARMOR ||
            isArmorSlot(event.getSlot()) ||
            isArmorSlot(event.getRawSlot());

        if (!armorSlotInvolved) return;

        new BukkitRunnable() {
            @Override public void run() {
                if (p.isOnline()) updatePlayerEffects(p);
            }
        }.runTaskLater(plugin, 1L);
    }

    public void updatePlayerEffects(Player player) {
        String detectedSetId = detectFullSet(player);
        String previousSetId = activeSetCache.get(player.getUniqueId());

        if (Objects.equals(detectedSetId, previousSetId)) return;

        if (previousSetId != null) {
            ArmorSet oldSet = plugin.getArmorSetManager().getSet(previousSetId);
            if (oldSet != null) removeEffects(player, oldSet);
            player.sendMessage("§8[§6OnyxArmor§8] §cSet removed: " +
                (oldSet != null ? oldSet.getDisplayName() : previousSetId));
        }

        if (detectedSetId != null) {
            ArmorSet newSet = plugin.getArmorSetManager().getSet(detectedSetId);
            if (newSet != null) {
                applyEffects(player, newSet);
                player.sendMessage("§8[§6OnyxArmor§8] §aFull set equipped: " + newSet.getDisplayName());
            }
        }

        if (detectedSetId != null) {
            activeSetCache.put(player.getUniqueId(), detectedSetId);
        } else {
            activeSetCache.remove(player.getUniqueId());
        }
    }

    private String detectFullSet(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] armorSlots = new ItemStack[]{
            inv.getHelmet(),
            inv.getChestplate(),
            inv.getLeggings(),
            inv.getBoots()
        };

        String foundId = null;
        for (ItemStack piece : armorSlots) {
            String pieceSetId = ItemUtils.getSetId(piece);
            if (pieceSetId == null) return null;
            if (foundId == null) {
                foundId = pieceSetId;
            } else if (!foundId.equals(pieceSetId)) {
                return null;
            }
        }
        return foundId;
    }

    private void applyEffects(Player player, ArmorSet set) {
        for (Map.Entry<PotionEffectType, Integer> entry : set.getEffects().entrySet()) {
            player.addPotionEffect(new PotionEffect(
                entry.getKey(),
                Integer.MAX_VALUE,
                entry.getValue(),
                true,
                false,
                false
            ));
        }
    }

    private void removeEffects(Player player, ArmorSet set) {
        for (PotionEffectType type : set.getEffects().keySet()) {
            player.removePotionEffect(type);
        }
    }

    private boolean isArmorSlot(int slot) {
        return slot >= 36 && slot <= 39;
    }
}
