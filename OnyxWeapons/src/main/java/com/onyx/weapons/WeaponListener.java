package com.onyx.weapons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponListener implements Listener {

    private final OnyxWeapons plugin;

    public WeaponListener(OnyxWeapons plugin) {
        this.plugin = plugin;
    }

    /**
     * When a player crafts an OnyxWeapons item, replace the result with
     * the properly built item that has all correct attribute modifiers.
     * ShapedRecipe result items can lose attribute modifiers in Paper 1.21.1
     * so we always rebuild the item fresh on craft.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        String weaponId = plugin.getRegistry().getWeaponId(result);
        if (weaponId == null) return;

        Weapon weapon = plugin.getRegistry().getWeapon(weaponId);
        if (weapon == null) return;

        event.setCurrentItem(plugin.getRegistry().buildItem(weapon));
    }
}
