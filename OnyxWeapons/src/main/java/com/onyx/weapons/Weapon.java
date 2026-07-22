package com.onyx.weapons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Weapon {

    private final String id;
    private final String displayName;
    private final Material material;
    private final double damage;
    private final double attackSpeed; // final attack speed value e.g. 1.6, 0.9
    private final int customModelData; // Added for resource pack textures
    private final List<String> lore;
    private final ItemStack[] recipe; // 9-slot grid, null = air

    public Weapon(String id, String displayName, Material material,
                  double damage, double attackSpeed, int customModelData,
                  List<String> lore, ItemStack[] recipe) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.customModelData = customModelData;
        this.lore = lore;
        this.recipe = recipe;
    }

    public String getId()            { return id; }
    public String getDisplayName()   { return displayName; }
    public Material getMaterial()    { return material; }
    public double getDamage()        { return damage; }
    public double getAttackSpeed()   { return attackSpeed; }
    public int getCustomModelData()  { return customModelData; }
    public List<String> getLore()    { return lore; }
    public ItemStack[] getRecipe()   { return recipe; }
}
