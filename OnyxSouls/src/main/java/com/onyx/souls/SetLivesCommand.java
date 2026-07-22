package com.onyx.souls;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLivesCommand implements CommandExecutor {

    private final OnyxSouls plugin;

    public SetLivesCommand(OnyxSouls plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Permission check
        if (!sender.hasPermission("onyx.admin") && !sender.isOp()) {
            sender.sendMessage("§cThe Abyss denies your authority.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUsage: /setlives <player> <amount>");
            return true;
        }

        // Resolve target — supports offline players by name if online
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer §e" + args[0] + " §cis not online.");
            return true;
        }

        // Parse and validate amount
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c'" + args[1] + "' is not a valid number.");
            return true;
        }

        // FIX: Enforce min/max bounds — previously any integer was accepted,
        //      which allowed values like 66 (seen in data.yml during audit).
        int maxLives = plugin.getMaxLives();
        if (amount < 0 || amount > maxLives) {
            sender.sendMessage("§cLives must be between §e0 §cand §e" + maxLives + "§c.");
            return true;
        }

        // Apply
        String path = "players." + target.getUniqueId() + ".lives";
        plugin.getData().set(path, amount);
        plugin.saveData();

        sender.sendMessage("§6§lONYX §8» §7Set §e" + target.getName() + "'s §7souls to §c" + amount + "§7.");
        target.sendMessage("§6§lONYX §8» §7An administrator has set your souls to §c" + amount + "§7.");

        // If an admin sets lives to 0, broadcast the banishment
        if (amount <= 0) {
            String msg = plugin.getMessage("banished").replace("{player}", target.getName());
            org.bukkit.Bukkit.broadcastMessage(msg);
        }

        return true;
    }
}
