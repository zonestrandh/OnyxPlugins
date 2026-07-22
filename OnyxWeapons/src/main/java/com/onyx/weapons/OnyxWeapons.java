package com.onyx.weapons;

import org.bukkit.plugin.java.JavaPlugin;

public class OnyxWeapons extends JavaPlugin {

    private static OnyxWeapons instance;
    private WeaponRegistry registry;

    @Override
    public void onEnable() {
        instance = this;
        registry = new WeaponRegistry(this);
        registry.registerAll();

        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);

        OnyxWeaponsCommand cmd = new OnyxWeaponsCommand(this);
        getCommand("onyxweapons").setExecutor(cmd);
        getCommand("onyxweapons").setTabCompleter(cmd);

        getLogger().info("OnyxWeapons enabled — " + registry.count() + " weapons forged.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OnyxWeapons disabled.");
    }

    public static OnyxWeapons get() { return instance; }
    public WeaponRegistry getRegistry() { return registry; }
}
