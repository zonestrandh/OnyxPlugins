package com.onyx.souls;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class OnyxSouls extends JavaPlugin {

    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createDataConfig();

        getServer().getPluginManager().registerEvents(new SoulListener(this), this);

        getCommand("revive").setExecutor(new ReviveCommand(this));
        getCommand("setlives").setExecutor(new SetLivesCommand(this));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new OnyxPlaceholder(this).register();
            getLogger().info("PlaceholderAPI hooked successfully.");
        } else {
            getLogger().warning("PlaceholderAPI not found — placeholders will not work.");
        }

        getLogger().info("OnyxSouls v1.2 enabled. May the Abyss spare your soul.");
    }

    @Override
    public void onDisable() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLogger().info("OnyxSouls disabled. Data saved.");
    }

    // ── Data config ──────────────────────────────────────────────────────────

    public FileConfiguration getData() {
        return this.dataConfig;
    }

    public void saveData() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                dataConfig.save(dataFile);
            } catch (IOException e) {
                getLogger().severe("Failed to save data.yml: " + e.getMessage());
            }
        });
    }

    private void createDataConfig() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    // ── Config helpers ───────────────────────────────────────────────────────

    public int getDefaultLives() {
        return getConfig().getInt("souls.default-lives", 3);
    }

    public int getMaxLives() {
        return getConfig().getInt("souls.max-lives", 10);
    }

    public int getBanishX() { return getConfig().getInt("banishment.x", 0); }
    public int getBanishY() { return getConfig().getInt("banishment.y", 100); }
    public int getBanishZ() { return getConfig().getInt("banishment.z", 0); }

    public String getMessage(String key) {
        return getConfig().getString("messages." + key, "§cMessage not configured: " + key);
    }

    // ── PlaceholderAPI expansion — %onyx_lives% ───────────────────────────

    public class OnyxPlaceholder extends PlaceholderExpansion {
        private final OnyxSouls plugin;

        public OnyxPlaceholder(OnyxSouls plugin) {
            this.plugin = plugin;
        }

        @Override public String getIdentifier() { return "onyx"; }
        @Override public String getAuthor()     { return "Onyx"; }
        @Override public String getVersion()    { return "1.2"; }
        @Override public boolean persist()      { return true; }

        @Override
        public String onPlaceholderRequest(Player p, String params) {
            if (p == null) return "";
            if (params.equals("lives")) {
                return String.valueOf(
                    plugin.getData().getInt(
                        "players." + p.getUniqueId() + ".lives",
                        plugin.getDefaultLives()
                    )
                );
            }
            return null;
        }
    }
}
