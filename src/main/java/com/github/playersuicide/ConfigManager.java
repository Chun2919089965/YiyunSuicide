package com.github.playersuicide;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfigManager {

    @NotNull
    private final JavaPlugin plugin;

    @Nullable
    private FileConfiguration config;

    @NotNull
    private final File configFile;

    public ConfigManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        initializeConfig();
    }

    private void initializeConfig() {
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        reloadConfig();
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        try (InputStream defConfigStream = plugin.getResource("config.yml")) {
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)
                );
                config.setDefaults(defConfig);
                config.options().copyDefaults(true);
            }
        } catch (IOException e) {
        }
    }

    public void saveConfig() {
        if (config != null) {
            try {
                config.save(configFile);
            } catch (IOException e) {
            }
        }
    }

    @NotNull
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @NotNull
    public List<String> getWorldBlacklist() {
        return getConfig().getStringList("world-blacklist");
    }

    public boolean isWorldBlacklistEnabled() {
        return getConfig().getBoolean("enable-blacklist", true);
    }

    @NotNull
    public List<String> getWorldWhitelist() {
        return getConfig().getStringList("world-whitelist");
    }

    public boolean isWorldWhitelistEnabled() {
        return getConfig().getBoolean("enable-whitelist", false);
    }

    public boolean isCooldownEnabled() {
        return getConfig().getBoolean("enable-cooldown", true);
    }

    public long getCooldownSeconds() {
        return Math.max(0, getConfig().getLong("cooldown-seconds", 10));
    }

    @NotNull
    public String getBroadcastMessage() {
        return getConfig().getString("broadcast-message",
            "&c&l☠ &7 玩家 &e&l{player} &7 选择了结束自己的生命 &c&l☠");
    }

    @NotNull
    public String getWorldDeniedMessage() {
        return getConfig().getString("world-denied-message",
            "&c 此世界不允许使用自杀功能！");
    }

    @NotNull
    public String getCooldownMessage() {
        return getConfig().getString("cooldown-message",
            "&c 自杀冷却中，请等待 &e{seconds} &c 秒");
    }

    public boolean isMetricsEnabled() {
        return getConfig().getBoolean("enable-metrics", true);
    }
}
