package com.github.playersuicide;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SuicideCommand implements CommandExecutor, TabCompleter, Listener {

    @NotNull
    private final Map<UUID, Long> cooldownMap = new ConcurrentHashMap<>();

    @NotNull
    private final Set<UUID> suicideFlags = ConcurrentHashMap.newKeySet();

    @NotNull
    private final ConfigManager configManager;

    public SuicideCommand(@NotNull ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("yiyunsuicide.reload")) {
                sender.sendMessage(configManager.getNoPermissionReloadMessage());
                return true;
            }
            configManager.reloadConfig();
            sender.sendMessage("§a§l✓ 配置文件已重新加载！");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(configManager.getPlayerOnlyMessage());
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("yiyunsuicide.killme")) {
            player.sendMessage(configManager.getNoPermissionMessage());
            return true;
        }

        if (!player.hasPermission("yiyunsuicide.bypass.world") && isWorldDenied(player.getWorld().getName())) {
            player.sendMessage(configManager.getWorldDeniedMessage());
            return true;
        }

        if (!player.hasPermission("yiyunsuicide.bypass") && configManager.isCooldownEnabled() && !checkCooldown(player)) {
            return true;
        }

        executeSuicide(player);
        return true;
    }

    private void executeSuicide(@NotNull Player player) {
        suicideFlags.add(player.getUniqueId());
        player.setHealth(0);
        String formattedMsg = configManager.getBroadcastMessage().replace("{player}", player.getName());
        Bukkit.broadcastMessage(formattedMsg);
    }

    private boolean checkCooldown(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        Long lastUsage = cooldownMap.get(playerId);
        if (lastUsage != null) {
            long remaining = configManager.getCooldownSeconds() * 1000L - (currentTime - lastUsage);

            if (remaining > 0) {
                long seconds = (remaining + 999) / 1000;
                String formattedMsg = configManager.getCooldownMessage().replace("{seconds}", String.valueOf(seconds));
                player.sendMessage(formattedMsg);
                return false;
            }
        }

        cooldownMap.put(playerId, currentTime);
        return true;
    }

    private boolean isWorldDenied(@NotNull String worldName) {
        if (configManager.isWorldBlacklistEnabled() && configManager.getWorldBlacklist().contains(worldName)) {
            return true;
        }

        if (configManager.isWorldWhitelistEnabled() && !configManager.getWorldWhitelist().contains(worldName)) {
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("yiyunsuicide.reload")) {
            return Collections.singletonList("reload");
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        if (suicideFlags.remove(event.getEntity().getUniqueId())) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (configManager.isCooldownEnabled()) {
            cooldownMap.remove(event.getPlayer().getUniqueId());
        }
    }
}
