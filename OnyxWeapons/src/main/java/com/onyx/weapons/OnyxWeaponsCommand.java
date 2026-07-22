package com.onyx.weapons;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnyxWeaponsCommand implements CommandExecutor, TabCompleter {

    private final OnyxWeapons plugin;

    public OnyxWeaponsCommand(OnyxWeapons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("onyxweapons.admin") && !sender.isOp()) {
            sender.sendMessage("§8[§6OnyxWeapons§8] §cThe forge denies your authority.");
            return true;
        }

        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "give":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /onyxweapons give <player> <weapon>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§8[§6OnyxWeapons§8] §cPlayer §e" + args[1] + " §cnot found.");
                    return true;
                }
                Weapon weapon = plugin.getRegistry().getWeapon(args[2].toLowerCase());
                if (weapon == null) {
                    sender.sendMessage("§8[§6OnyxWeapons§8] §cUnknown weapon: §e" + args[2]);
                    return true;
                }
                target.getInventory().addItem(plugin.getRegistry().buildItem(weapon));
                sender.sendMessage("§8[§6OnyxWeapons§8] §aGave " + weapon.getDisplayName() + " §ato §e" + target.getName());
                target.sendMessage("§8[§6OnyxWeapons§8] §aYou received a " + weapon.getDisplayName());
                return true;

            case "list":
                sender.sendMessage("§6§l=== OnyxWeapons ===");
                for (Weapon w : plugin.getRegistry().all()) {
                    sender.sendMessage("§8• §f" + w.getId()
                        + " §8— " + w.getDisplayName()
                        + " §8(§9" + w.getDamage() + " dmg §8| §a" + w.getAttackSpeed() + " spd§8)");
                }
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage("§6§lOnyxWeapons");
        s.sendMessage("§8/§fonyxweapons give §7<player> <weapon>");
        s.sendMessage("§8/§fonyxweapons list");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("onyxweapons.admin") && !sender.isOp()) return new ArrayList<>();
        if (args.length == 1)
            return Arrays.asList("give", "list").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        if (args.length == 2 && args[0].equalsIgnoreCase("give"))
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        if (args.length == 3 && args[0].equalsIgnoreCase("give"))
            return plugin.getRegistry().all().stream()
                .map(Weapon::getId)
                .filter(s -> s.startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
