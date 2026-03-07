package com.github.playersuicide;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SuicidePlugin extends JavaPlugin {

    @NotNull
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        SuicideCommand command = new SuicideCommand(configManager);
        getCommand("killme").setExecutor(command);
        getCommand("killme").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(command, this);

        // 注册 bStats 统计
        if (configManager.isMetricsEnabled()) {
            int pluginId = 29976;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    @Override
    public void onDisable() {
        if (configManager != null) {
            configManager.saveConfig();
        }
    }

    @NotNull
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
