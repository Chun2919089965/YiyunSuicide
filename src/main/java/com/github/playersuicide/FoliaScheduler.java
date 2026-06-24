package com.github.playersuicide;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class FoliaScheduler {

    private static final boolean IS_FOLIA;
    private static final Method GS_RUN;
    private static final Object GS_INSTANCE;

    static {
        boolean folia = false;
        Method run = null;
        Object instance = null;
        try {
            Method getter = Bukkit.class.getMethod("getGlobalRegionScheduler");
            instance = getter.invoke(null);
            Class<?> gsClass = Class.forName(
                "io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler"
            );
            for (Method m : gsClass.getMethods()) {
                if (m.getName().equals("run") && m.getParameterCount() == 2) {
                    run = m;
                    break;
                }
            }
            folia = (instance != null && run != null);
        } catch (Exception ignored) {
        }
        IS_FOLIA = folia;
        GS_INSTANCE = instance;
        GS_RUN = run;
    }

    private FoliaScheduler() {
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    @SuppressWarnings("unchecked")
    public static void runGlobalTask(@NotNull Plugin plugin, @NotNull Runnable task) {
        if (IS_FOLIA) {
            try {
                GS_RUN.invoke(GS_INSTANCE, plugin, (Consumer<Object>) st -> task.run());
                return;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING,
                    "[YiyunSuicide] Folia 全局调度异常，已回退直接执行", e);
            }
        }
        task.run();
    }

    @SuppressWarnings("unchecked")
    public static void runEntityTask(@NotNull Plugin plugin, @NotNull Entity entity,
                                     @NotNull Runnable task) {
        if (IS_FOLIA) {
            try {
                Method getScheduler = entity.getClass().getMethod("getScheduler");
                Object es = getScheduler.invoke(entity);
                Method run = es.getClass().getMethod("run",
                    Plugin.class, Consumer.class, Runnable.class);
                run.invoke(es, plugin, (Consumer<Object>) st -> task.run(), null);
                return;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING,
                    "[YiyunSuicide] Folia 实体调度异常，已回退直接执行", e);
            }
        }
        task.run();
    }
}
