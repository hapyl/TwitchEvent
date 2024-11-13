package me.hapyl.twitch.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Message {

    private static final String PREFIX = "&8[&5Twitch&8] ";

    public static void info(Object message) {
        send("&7" + message, "&f", "&7");
    }

    public static void success(Object message) {
        send("&2✔ &a" + message, "&2", "&a");
    }

    public static void error(Object message) {
        send("&4❌ &c" + message, "&4", "&c");
    }

    public static void debug(Object message) {
        send("&c[DEBUG] &e" + message, "&e", "&c");
    }

    private static void send(Object message, String color0, String color1) {
        final String formattedMessage = ChatColor.translateAlternateColorCodes(
                '&',
                PREFIX + colorFormat(String.valueOf(message), color0, color1)
        );

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(formattedMessage));
        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }

    private static String colorFormat(String string, String color0, String color1) {
        return string.replaceAll("\\{([^}]*)}", color0 + "$1" + color1);
    }


}
