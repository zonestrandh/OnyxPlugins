package com.onyx.armor;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemUtils {

    private static NamespacedKey SET_KEY;

    public static void init(JavaPlugin plugin) {
        SET_KEY = new NamespacedKey(plugin, "armor_set");
    }

    /**
     * Tags an ItemStack with the given set ID using PersistentDataContainer.
     * This survives server restarts and is invisible to players.
     */
    public static void tagItem(ItemStack item, String setId) {
        if (item == null || SET_KEY == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(SET_KEY, PersistentDataType.STRING, setId);
        item.setItemMeta(meta);
    }

    /**
     * Returns the set ID stored in an item's PersistentDataContainer,
     * or null if the item has no set tag.
     */
    public static String getSetId(ItemStack item) {
        if (item == null || SET_KEY == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.getOrDefault(SET_KEY, PersistentDataType.STRING, null);
    }

    public static boolean hasSetTag(ItemStack item) {
        return getSetId(item) != null;
    }
}
