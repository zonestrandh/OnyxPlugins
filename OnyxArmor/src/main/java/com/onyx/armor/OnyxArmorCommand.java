package com.onyx.armor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OnyxArmorCommand implements CommandExecutor, TabCompleter {

    private final OnyxArmor plugin;

    public OnyxArmorCommand(OnyxArmor plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("onyxarmor.admin") && !sender.isOp()) {
            sender.sendMessage("§8[§6OnyxArmor§8] §cThe forge denies your authority.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "give":
                return handleGive(sender, args);

            case "list":
                return handleList(sender);

            case "reload":
                plugin.getArmorSetManager().loadSets();
                sender.sendMessage("§8[§6OnyxArmor§8] §aArmor sets reloaded. " +
                    plugin.getArmorSetManager().getSetCount() + " sets loaded.");
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /onyxarmor give <player> <set>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§8[§6OnyxArmor§8] §cPlayer §e" + args[1] + " §cnot found.");
            return true;
        }

        ArmorSet set = plugin.getArmorSetManager().getSet(args[2]);
        if (set == null) {
            sender.sendMessage("§8[§6OnyxArmor§8] §cUnknown armor set: §e" + args[2]);
            sender.sendMessage("§8[§6OnyxArmor§8] §7Use §f/onyxarmor list §7to see available sets.");
            return true;
        }

        // Build 4 independent pieces and give them
        ItemStack[] pieces = plugin.getArmorSetManager().buildArmorPieces(set);
        for (ItemStack piece : pieces) {
            if (piece != null) {
                target.getInventory().addItem(piece);
            }
        }

        sender.sendMessage("§8[§6OnyxArmor§8] §aGave " + set.getDisplayName() + " §ato §e" + target.getName() + "§a.");
        target.sendMessage("§8[§6OnyxArmor§8] §aYou received the " + set.getDisplayName() + " §aarmor set!");
        return true;
    }

    private boolean handleList(CommandSender sender) {
        Map<String, ArmorSet> sets = plugin.getArmorSetManager().getAllSets();
        sender.sendMessage("§6§l=== OnyxArmor Sets ===");
        if (sets.isEmpty()) {
            sender.sendMessage("§7No armor sets loaded.");
            return true;
        }
        for (Map.Entry<String, ArmorSet> entry : sets.entrySet()) {
            sender.sendMessage("§8• §f" + entry.getKey() + " §8- " + entry.getValue().getDisplayName());
        }
        sender.sendMessage("§7Use §f/onyxarmor give <player> <set> §7to give a set.");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§lOnyxArmor Commands");
        sender.sendMessage("§8/§fonyxarmor give §7<player> <set> §8- §7Give an armor set");
        sender.sendMessage("§8/§fonyxarmor list §8- §7List all available sets");
        sender.sendMessage("§8/§fonyxarmor reload §8- §7Reload armor set configs");
    }

    // -------------------------------------------------------------------------
    // Tab completion
    // -------------------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("onyxarmor.admin") && !sender.isOp()) return new ArrayList<>();

        if (args.length == 1) {
            return Arrays.asList("give", "list", "reload").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return new ArrayList<>(plugin.getArmorSetManager().getAllSets().keySet()).stream()
                .filter(s -> s.startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
