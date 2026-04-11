package com.github.playersuicide;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    @NotNull
    private final JavaPlugin plugin;

    @NotNull
    private final File configFile;

    @NotNull
    private FileConfiguration config;

    @NotNull
    private Set<String> worldBlacklist = Collections.emptySet();

    @NotNull
    private Set<String> worldWhitelist = Collections.emptySet();

    private boolean worldBlacklistEnabled;
    private boolean worldWhitelistEnabled;
    private boolean cooldownEnabled;
    private long cooldownSeconds;
    private boolean metricsEnabled;

    @NotNull
    private String broadcastMessage;
    @NotNull
    private String worldDeniedMessage;
    @NotNull
    private String cooldownMessage;

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
            plugin.getLogger().warning("加载默认配置时出错: " + e.getMessage());
        }

        loadCachedValues();
    }

    private void loadCachedValues() {
        worldBlacklistEnabled = config.getBoolean("enable-blacklist", true);
        worldWhitelistEnabled = config.getBoolean("enable-whitelist", false);
        cooldownEnabled = config.getBoolean("enable-cooldown", true);
        cooldownSeconds = Math.max(0, config.getLong("cooldown-seconds", 10));
        metricsEnabled = config.getBoolean("enable-metrics", true);

        worldBlacklist = new HashSet<>(config.getStringList("world-blacklist"));
        worldWhitelist = new HashSet<>(config.getStringList("world-whitelist"));

        broadcastMessage = ChatColor.translateAlternateColorCodes('&', config.getString("broadcast-message",
            "&c&l☠ &7 玩家 &e&l{player} &7 选择了结束自己的生命 &c&l☠"));
        worldDeniedMessage = ChatColor.translateAlternateColorCodes('&', config.getString("world-denied-message",
            "&c 此世界不允许使用自杀功能！"));
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', config.getString("cooldown-message",
            "&c 自杀冷却中，请等待 &e{seconds} &c 秒"));
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("保存配置文件时出错: " + e.getMessage());
        }
    }

    @NotNull
    public Set<String> getWorldBlacklist() {
        return worldBlacklist;
    }

    public boolean isWorldBlacklistEnabled() {
        return worldBlacklistEnabled;
    }

    @NotNull
    public Set<String> getWorldWhitelist() {
        return worldWhitelist;
    }

    public boolean isWorldWhitelistEnabled() {
        return worldWhitelistEnabled;
    }

    public boolean isCooldownEnabled() {
        return cooldownEnabled;
    }

    public long getCooldownSeconds() {
        return cooldownSeconds;
    }

    @NotNull
    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    @NotNull
    public String getWorldDeniedMessage() {
        return worldDeniedMessage;
    }

    @NotNull
    public String getCooldownMessage() {
        return cooldownMessage;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }
}
