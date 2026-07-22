package com.onyx.souls;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReviveCommand implements CommandExecutor {

    private final OnyxSouls plugin;

    public ReviveCommand(OnyxSouls plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Only players can perform a revive ritual
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly a living player may perform the revive ritual.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length != 1) {
            p.sendMessage("§cUsage: /revive <player>");
            return true;
        }

        // Read cost from config
        String materialName = plugin.getConfig().getString("revive.cost-material", "DIAMOND");
        int costAmount = plugin.getConfig().getInt("revive.cost-amount", 5);
        Material costMaterial;

        try {
            costMaterial = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            p.sendMessage("§c[OnyxSouls] Invalid cost-material in config.yml. Contact an admin.");
            plugin.getLogger().severe("Invalid revive.cost-material in config.yml: " + materialName);
            return true;
        }

        // Find target
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage("§cThat soul cannot be found.");
            return true;
        }

        // Can't revive yourself
        if (target.equals(p)) {
            p.sendMessage("§cYou cannot recall your own soul from the Abyss.");
            return true;
        }

        // Check target is actually banished
        int targetLives = plugin.getData().getInt("players." + target.getUniqueId() + ".lives", plugin.getDefaultLives());
        if (targetLives > 0) {
            p.sendMessage("§e" + target.getName() + " §7still draws breath. No ritual is needed.");
            return true;
        }

        // Check reviver has the cost items
        if (!p.getInventory().contains(costMaterial, costAmount)) {
            p.sendMessage("§cThe ritual requires §b" + costAmount + " " + formatMaterialName(costMaterial) + "§c.");
            return true;
        }

        // Find the Overworld to send the revived player to
        World overworld = null;
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                overworld = w;
                break;
            }
        }

        if (overworld == null) {
            p.sendMessage("§c[OnyxSouls] Could not find the Overworld. Contact an admin.");
            plugin.getLogger().severe("Could not find a NORMAL environment world for revive!");
            return true;
        }

        // Perform the ritual
        p.getInventory().removeItem(new ItemStack(costMaterial, costAmount));

        plugin.getData().set("players." + target.getUniqueId() + ".lives", 1);
        plugin.saveData();

        Location spawn = overworld.getSpawnLocation();
        target.teleport(spawn);

        // Notify everyone
        String broadcast = plugin.getMessage("revived")
            .replace("{reviver}", p.getName())
            .replace("{target}", target.getName());
        Bukkit.broadcastMessage(broadcast);

        return true;
    }

    /**
     * Converts a Material enum name to a readable string.
     * e.g. DIAMOND -> Diamond, GOLD_INGOT -> Gold Ingot
     */
    private String formatMaterialName(Material mat) {
        String[] words = mat.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
              .append(word.substring(1))
              .append(" ");
        }
        return sb.toString().trim();
    }
}
