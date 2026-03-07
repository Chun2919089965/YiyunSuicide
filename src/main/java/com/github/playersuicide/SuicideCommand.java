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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SuicideCommand implements CommandExecutor, TabCompleter, Listener {

    @NotNull
    private final Map<UUID, Long> cooldownMap = new ConcurrentHashMap<>();

    @NotNull
    private final ConfigManager configManager;

    private static final String COLOR_CHAR = "&";
    private static final String ALT_COLOR_CHAR = "§";

    public SuicideCommand(@NotNull ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("yiyunsuicide.reload")) {
                    sender.sendMessage("§c你没有权限执行此命令！");
                    return true;
                }
                configManager.reloadConfig();
                sender.sendMessage("§a§l✓ 配置文件已重新加载！");
                return true;
            }
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("yiyunsuicide.reload")) {
                player.sendMessage("§c你没有权限执行此命令！");
                return true;
            }
            configManager.reloadConfig();
            player.sendMessage("§a§l✓ 配置文件已重新加载！");
            return true;
        }

        if (!player.hasPermission("yiyunsuicide.killme")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (isWorldDenied(player.getWorld().getName())) {
            sendColoredMessage(player, configManager.getWorldDeniedMessage());
            return true;
        }

        if (configManager.isCooldownEnabled()) {
            if (!checkCooldown(player)) {
                return true;
            }
        }

        executeSuicide(player);
        return true;
    }

    private void executeSuicide(@NotNull Player player) {
        player.setHealth(0);
        String broadcastMsg = configManager.getBroadcastMessage();
        String formattedMsg = formatMessage(broadcastMsg, player.getName());
        Bukkit.broadcastMessage(formattedMsg);
    }

    private boolean checkCooldown(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        Long lastUsage = cooldownMap.get(playerId);
        if (lastUsage != null) {
            long timePassed = currentTime - lastUsage;
            long cooldownMillis = configManager.getCooldownSeconds() * 1000L;
            long remaining = cooldownMillis - timePassed;

            if (remaining > 0) {
                long seconds = remaining / 1000;
                String cooldownMsg = configManager.getCooldownMessage();
                String formattedMsg = cooldownMsg.replace("{seconds}", String.valueOf(seconds));
                sendColoredMessage(player, formattedMsg);
                return false;
            }
        }

        cooldownMap.put(playerId, currentTime);
        return true;
    }

    private boolean isWorldDenied(@NotNull String worldName) {
        if (configManager.isWorldBlacklistEnabled()) {
            if (configManager.getWorldBlacklist().contains(worldName)) {
                return true;
            }
        }

        if (configManager.isWorldWhitelistEnabled()) {
            if (!configManager.getWorldWhitelist().contains(worldName)) {
                return true;
            }
        }

        return false;
    }

    private void sendColoredMessage(@NotNull Player player, @NotNull String message) {
        player.sendMessage(message.replace(COLOR_CHAR, ALT_COLOR_CHAR));
    }

    @NotNull
    private String formatMessage(@NotNull String message, @NotNull String playerName) {
        return message.replace("{player}", playerName).replace(COLOR_CHAR, ALT_COLOR_CHAR);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("yiyunsuicide.reload")) {
                return java.util.Collections.singletonList("reload");
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (cooldownMap.containsKey(player.getUniqueId())) {
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
