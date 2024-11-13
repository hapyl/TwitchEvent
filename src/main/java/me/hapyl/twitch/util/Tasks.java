package me.hapyl.twitch.util;

import me.hapyl.twitch.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

public class Tasks {

    public static void later(@NonNull Runnable runnable, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(Main.getPlugin(), delay);
    }

}
