package com.onyx.armor;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ArmorSetManager {

    private final OnyxArmor plugin;
    private final Map<String, ArmorSet> sets = new LinkedHashMap<>();

    private static final String[] SLOTS = {"_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS"};

    public ArmorSetManager(OnyxArmor plugin) {
        this.plugin = plugin;
    }

    public void loadSets() {
        sets.clear();

        File file = new File(plugin.getDataFolder(), "armor_sets.yml");
        if (!file.exists()) {
            plugin.saveResource("armor_sets.yml", false);
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for (String key : cfg.getKeys(false)) {
            if (!cfg.isConfigurationSection(key)) continue;

            try {
                ArmorSet set = parseSet(key, cfg);
                sets.put(key.toLowerCase(), set);
                plugin.getLogger().info("Loaded armor set: " + key);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load armor set '" + key + "': " + e.getMessage());
            }
        }
    }

    private ArmorSet parseSet(String id, FileConfiguration cfg) {
        String path = id + ".";

        String display = colorize(cfg.getString(path + "display", id));

        String matName = cfg.getString(path + "material", "LEATHER").toUpperCase();
        Material baseMaterial = resolveMaterial(matName);

        Color color = null;
        if (baseMaterial == Material.LEATHER_CHESTPLATE) {
            String colorStr = cfg.getString(path + "color", null);
            if (colorStr != null) {
                String[] parts = colorStr.split(",");
                if (parts.length == 3) {
                    color = Color.fromRGB(
                            Integer.parseInt(parts[0].trim()),
                            Integer.parseInt(parts[1].trim()),
                            Integer.parseInt(parts[2].trim())
                    );
                }
            }
        }

        List<String> lore = cfg.getStringList(path + "lore")
                .stream()
                .map(this::colorize)
                .collect(Collectors.toList());

        Map<PotionEffectType, Integer> effects = new LinkedHashMap<>();
        for (String effectStr : cfg.getStringList(path + "effects")) {
            effectStr = effectStr.split("#")[0].trim();
            if (effectStr.isEmpty()) continue;

            String[] parts = effectStr.split(":");

            PotionEffectType type = resolveEffect(parts[0].trim().toUpperCase());
            if (type == null) {
                plugin.getLogger().warning("Unknown effect '" + parts[0] + "' in set '" + id + "'");
                continue;
            }

            int level = parts.length >= 2 ? Integer.parseInt(parts[1].trim()) : 1;
            effects.put(type, Math.max(0, level - 1));
        }

        return new ArmorSet(
                id,
                display,
                baseMaterial,
                color,
                lore,
                effects,
                cfg.getBoolean(path + "glow", false),
                cfg.getBoolean(path + "partial_bonus", false)
        );
    }

    public ItemStack[] buildArmorPieces(ArmorSet set) {
        ItemStack[] pieces = new ItemStack[4];
        Material[] materials = getMaterials(set.getMaterial());

        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(materials[i]);
            ItemMeta meta = item.getItemMeta();

            if (meta == null) continue;

            meta.setDisplayName(set.getDisplayName());
            meta.setLore(set.getLore());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            if (meta instanceof LeatherArmorMeta leather && set.isLeather() && set.getLeatherColor() != null) {
                leather.setColor(set.getLeatherColor());
            }

            // ✅ FIXED HERE
            if (set.isGlow()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
            ItemUtils.tagItem(item, set.getId());

            pieces[i] = item;
        }

        return pieces;
    }

    private Material resolveMaterial(String name) {
        return switch (name) {
            case "DIAMOND" -> Material.DIAMOND_CHESTPLATE;
            case "IRON" -> Material.IRON_CHESTPLATE;
            case "GOLD", "GOLDEN" -> Material.GOLDEN_CHESTPLATE;
            case "CHAINMAIL" -> Material.CHAINMAIL_CHESTPLATE;
            case "NETHERITE" -> Material.NETHERITE_CHESTPLATE;
            default -> Material.LEATHER_CHESTPLATE;
        };
    }

    private Material[] getMaterials(Material base) {
        if (base == Material.DIAMOND_CHESTPLATE)
            return new Material[]{Material.DIAMOND_HELMET, base, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};
        if (base == Material.IRON_CHESTPLATE)
            return new Material[]{Material.IRON_HELMET, base, Material.IRON_LEGGINGS, Material.IRON_BOOTS};
        if (base == Material.GOLDEN_CHESTPLATE)
            return new Material[]{Material.GOLDEN_HELMET, base, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS};
        if (base == Material.CHAINMAIL_CHESTPLATE)
            return new Material[]{Material.CHAINMAIL_HELMET, base, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS};
        if (base == Material.NETHERITE_CHESTPLATE)
            return new Material[]{Material.NETHERITE_HELMET, base, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};

        return new Material[]{Material.LEATHER_HELMET, base, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
    }

    // ✅ FULLY FIXED EFFECT MAPPING
    private PotionEffectType resolveEffect(String name) {
        return switch (name) {
            case "INCREASE_DAMAGE", "STRENGTH" -> PotionEffectType.STRENGTH;
            case "DAMAGE_RESISTANCE", "RESISTANCE" -> PotionEffectType.RESISTANCE;
            case "JUMP", "JUMP_BOOST" -> PotionEffectType.JUMP_BOOST;
            case "FAST_DIGGING", "HASTE" -> PotionEffectType.HASTE;
            case "SLOW_DIGGING", "MINING_FATIGUE" -> PotionEffectType.MINING_FATIGUE;
            case "SLOW", "SLOWNESS" -> PotionEffectType.SLOWNESS;
            case "FIRE_RESISTANCE" -> PotionEffectType.FIRE_RESISTANCE;
            case "WATER_BREATHING" -> PotionEffectType.WATER_BREATHING;
            case "NIGHT_VISION" -> PotionEffectType.NIGHT_VISION;
            case "INVISIBILITY" -> PotionEffectType.INVISIBILITY;
            case "BLINDNESS" -> PotionEffectType.BLINDNESS;
            case "SPEED" -> PotionEffectType.SPEED;
            case "REGENERATION" -> PotionEffectType.REGENERATION;
            case "POISON" -> PotionEffectType.POISON;
            case "WITHER" -> PotionEffectType.WITHER;
            case "ABSORPTION" -> PotionEffectType.ABSORPTION;
            case "SATURATION" -> PotionEffectType.SATURATION;
            case "GLOWING" -> PotionEffectType.GLOWING;
            case "LEVITATION" -> PotionEffectType.LEVITATION;
            case "LUCK" -> PotionEffectType.LUCK;
            case "UNLUCK" -> PotionEffectType.UNLUCK;
            default -> PotionEffectType.getByName(name);
        };
    }

    private String colorize(String s) {
        return s == null ? "" : s.replace("&", "§");
    }

    public ArmorSet getSet(String id) {
        return sets.get(id.toLowerCase());
    }

    public Map<String, ArmorSet> getAllSets() {
        return Collections.unmodifiableMap(sets);
    }

    public int getSetCount() {
        return sets.size();
    }
}
