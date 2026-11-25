package com.onyx.weapons;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeaponRegistry {

    private final OnyxWeapons plugin;
    private final Map<String, Weapon> weapons = new LinkedHashMap<>();
    private NamespacedKey idKey;

    public WeaponRegistry(OnyxWeapons plugin) {
        this.plugin = plugin;
        this.idKey = new NamespacedKey(plugin, "onyx_weapon_id");
    }

    public void registerAll() {
        // KNIFE - Uses STONE_SWORD base (Matches custom_model_data 2001)
        add(new Weapon(
            "knife", "§7§lIron Knife", Material.STONE_SWORD,
            3.5, 2.0, 2001,
            List.of(
                "§7A short razor blade favoured",
                "§7by scouts and assassins.",
                "§7",
                "§8» §7Type§8: §fMelee — Fast",
                "§8» §7Damage§8: §93.5",
                "§8» §7Bonus§8: §aDeadly vs unarmoured"
            ),
            grid(
                n(), f(Material.FLINT),     n(),
                n(), f(Material.IRON_INGOT),n(),
                n(), n(),                   n()
            )
        ));

        // SABER - Uses GOLDEN_SWORD base (Matches custom_model_data 2002)
        add(new Weapon(
            "saber", "§e§lIron Saber", Material.GOLDEN_SWORD,
            4.5, 1.8, 2002,
            List.of(
                "§7A curved cavalry sword light",
                "§7enough to wield in either hand.",
                "§7",
                "§8» §7Type§8: §fMelee — Fast",
                "§8» §7Damage§8: §94.5",
                "§8» §7Bonus§8: §aDual wield capable"
            ),
            grid(
                n(), f(Material.IRON_INGOT), n(),
                n(), f(Material.IRON_INGOT), n(),
                n(), f(Material.STICK),      n()
            )
        ));

        // RAPIER - Uses IRON_SWORD base (Matches custom_model_data 2003)
        add(new Weapon(
            "rapier", "§f§lIron Rapier", Material.IRON_SWORD,
            5.0, 1.6, 2003,
            List.of(
                "§7A slender thrusting blade",
                "§7designed to pierce armour.",
                "§7",
                "§8» §7Type§8: §fMelee — Medium",
                "§8» §7Damage§8: §95",
                "§8» §7Bonus§8: §aArmour piercing"
            ),
            grid(
                n(), f(Material.IRON_INGOT), n(),
                n(), f(Material.IRON_INGOT), n(),
                n(), f(Material.STICK),      n()
            )
        ));

        // SPEAR - Uses IRON_SWORD base (Matches custom_model_data 2004)
        add(new Weapon(
            "spear", "§e§lIron Spear", Material.IRON_SWORD,
            6.0, 1.4, 2004,
            List.of(
                "§7A long reach pole weapon",
                "§7carried by infantry.",
                "§7",
                "§8» §7Type§8: §fMelee — Medium",
                "§8» §7Damage§8: §96",
                "§8» §7Bonus§8: §aPiercing thrust"
            ),
            grid(
                n(),              f(Material.IRON_INGOT), n(),
                n(),              f(Material.STICK),      n(),
                n(),              f(Material.STICK),      n()
            )
        ));

        // KATANA - Uses DIAMOND_SWORD base (Matches custom_model_data 2005)
        add(new Weapon(
            "katana", "§b§lIron Katana", Material.DIAMOND_SWORD,
            7.0, 1.7, 2005,
            List.of(
                "§7An elegant curved blade",
                "§7favoured by swift warriors.",
                "§7",
                "§8» §7Type§8: §fMelee — Medium-Fast",
                "§8» §7Damage§8: §97",
                "§8» §7Bonus§8: §aMobile, speed boost"
            ),
            grid(
                n(), f(Material.IRON_INGOT), n(),
                n(), f(Material.IRON_INGOT), n(),
                n(), f(Material.STICK),      n()
            )
        ));

        // SCYTHE - Uses IRON_HOE base (Matches custom_model_data 2006)
        add(new Weapon(
            "scythe", "§2§lIron Scythe", Material.IRON_HOE,
            8.0, 0.9, 2006,
            List.of(
                "§7A sweeping curved blade that",
                "§7cuts through armour with ease.",
                "§7",
                "§8» §7Type§8: §fMelee — Slow",
                "§8» §7Damage§8: §98",
                "§8» §7Bonus§8: §aCuts through armour"
            ),
            grid(
                f(Material.IRON_INGOT), f(Material.IRON_INGOT), n(),
                n(),                    f(Material.STICK),       n(),
                n(),                    f(Material.STICK),       n()
            )
        ));

        // LONGSWORD - Uses DIAMOND_SWORD base (Matches custom_model_data 2007)
        add(new Weapon(
            "longsword", "§6§lIron Longsword", Material.DIAMOND_SWORD,
            9.0, 1.1, 2007,
            List.of(
                "§7A heavy two-handed blade forged",
                "§7for knights of the realm.",
                "§7",
                "§8» §7Type§8: §fMelee — Slow",
                "§8» §7Damage§8: §99",
                "§8» §7Bonus§8: §aPowerful two handed"
            ),
            grid(
                n(),                    f(Material.IRON_INGOT), n(),
                n(),                    f(Material.IRON_INGOT), n(),
                n(),                    f(Material.STICK),      n()
            )
        ));

        // CLEAVER - Uses NETHERITE_AXE base (Matches custom_model_data 2008)
        add(new Weapon(
            "cleaver", "§4§lIron Cleaver", Material.NETHERITE_AXE,
            10.0, 0.7, 2008,
            List.of(
                "§7A massive heavy blade that",
                "§7devastates everything it hits.",
                "§7",
                "§8» §7Type§8: §fMelee — Very Slow",
                "§8» §7Damage§8: §910",
                "§8» §7Bonus§8: §aMassive damage output"
            ),
            grid(
                f(Material.IRON_INGOT), f(Material.IRON_INGOT), n(),
                f(Material.IRON_INGOT), f(Material.IRON_INGOT), n(),
                n(),                    f(Material.STICK),       n()
            )
        ));

        for (Weapon w : weapons.values()) {
            registerRecipe(w);
        }

        plugin.getLogger().info("Registered " + weapons.size() + " weapon recipes.");
    }

    public ItemStack buildItem(Weapon weapon) {
        ItemStack item = new ItemStack(weapon.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(weapon.getDisplayName());
        meta.setLore(weapon.getLore());

        // Assign Custom Model Data so the Resource Pack overrides the vanilla look
        meta.setCustomModelData(weapon.getCustomModelData());

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Keep them from breaking easily during your custom testing
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);

        // Strip default attack stats entirely so we can override them
        meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE);
        meta.removeAttributeModifier(Attribute.ATTACK_SPEED);

        // Base damage tracking
        NamespacedKey dmgKey = new NamespacedKey(plugin, weapon.getId() + "_damage");
        meta.addAttributeModifier(
            Attribute.ATTACK_DAMAGE,
            new AttributeModifier(
                dmgKey,
                weapon.getDamage() - 1.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
            )
        );

        // Base attack speed tracking
        NamespacedKey spdKey = new NamespacedKey(plugin, weapon.getId() + "_speed");
        double speedModifier = weapon.getAttackSpeed() - 4.0;
        meta.addAttributeModifier(
            Attribute.ATTACK_SPEED,
            new AttributeModifier(
                spdKey,
                speedModifier,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
            )
        );

        meta.getPersistentDataContainer().set(
            idKey, PersistentDataType.STRING, weapon.getId()
        );

        item.setItemMeta(meta);
        return item;
    }

    private void registerRecipe(Weapon weapon) {
        try {
            ItemStack result = buildItem(weapon);
            NamespacedKey key = new NamespacedKey(plugin, weapon.getId());
            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape("ABC", "DEF", "GHI");

            char[] chars = {'A','B','C','D','E','F','G','H','I'};
            ItemStack[] grid = weapon.getRecipe();

            for (int i = 0; i < 9; i++) {
                if (grid[i] != null) {
                    recipe.setIngredient(chars[i], grid[i].getType());
                }
            }

            plugin.getServer().addRecipe(recipe);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register recipe for " +
                weapon.getId() + ": " + e.getMessage());
        }
    }

    private void add(Weapon w) { weapons.put(w.getId(), w); }
    private ItemStack f(Material mat) { return new ItemStack(mat); }
    private ItemStack n() { return null; }
    private ItemStack[] grid(ItemStack a, ItemStack b, ItemStack c, ItemStack d, ItemStack e, ItemStack f, ItemStack g, ItemStack h, ItemStack i) {
        return new ItemStack[]{a, b, c, d, e, f, g, h, i};
    }
    public String getWeaponId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(idKey, PersistentDataType.STRING, null);
    }
    public Weapon getWeapon(String id) { return weapons.get(id.toLowerCase()); }
    public Collection<Weapon> all() { return weapons.values(); }
    public int count() { return weapons.size(); }
}
