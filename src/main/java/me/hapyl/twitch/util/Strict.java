package me.hapyl.twitch.util;

import me.hapyl.twitch.Main;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;
import java.util.logging.Logger;

public final class Strict {

    public static void notNull(@Nullable Object object, @NotNull String message) {
        if (object == null) {
            disablePlugin(message);
        }
    }

    public static void unsupportedOp(@NonNull String opName) {
        disablePlugin("Unable to '%s' because it's unsupported on this device!".formatted(opName));
    }

    @NonNull
    public static IllegalArgumentException exception(IllegalArgumentException exception) {
        disablePlugin(exception.getMessage());

        return exception;
    }

    @NonNull
    public static IllegalArgumentException illegalArgument(@NonNull String message) {
        return exception0(IllegalArgumentException::new, "Illegal argument! " + message);
    }

    @NonNull
    public static NullPointerException nullPointer(@NonNull String message) {
        return exception0(NullPointerException::new, message);
    }

    private static <T extends RuntimeException> T exception0(Function<String, T> t, String message) {
        disablePlugin(message);
        return t.apply(message);
    }

    private static void disablePlugin(String reason) {
        final Main plugin = Main.getPlugin();
        final Logger logger = plugin.getLogger();

        logger.severe("********************************");
        logger.severe("** " + reason + " **");
        logger.severe("********************************");

        Bukkit.getPluginManager().disablePlugin(plugin);
    }

}
