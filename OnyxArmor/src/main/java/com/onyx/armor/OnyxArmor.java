package com.onyx.armor;
 
import org.bukkit.plugin.java.JavaPlugin;
 
public class OnyxArmor extends JavaPlugin {
 
    private ArmorSetManager armorSetManager;
 
  @Override
public void onEnable() {
    armorSetManager = new ArmorSetManager(this);
    armorSetManager.loadSets();

    getServer().getPluginManager().registerEvents(new ArmorListener(this), this);

    OnyxArmorCommand cmd = new OnyxArmorCommand(this);
    getCommand("onyxarmor").setExecutor(cmd);
    getCommand("onyxarmor").setTabCompleter(cmd);

    getLogger().info("OnyxArmor enabled. " + armorSetManager.getSetCount() + " armor sets loaded.");
}
 
    @Override
    public void onDisable() {
        getLogger().info("OnyxArmor disabled.");
    }
 
    public ArmorSetManager getArmorSetManager() {
        return armorSetManager;
    }
}
 
