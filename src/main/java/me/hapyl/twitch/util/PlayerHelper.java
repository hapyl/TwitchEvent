package me.hapyl.twitch.util;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class PlayerHelper {

    public static void title(@NonNull Player player, @NonNull Object title, @NonNull Object subTitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(color(title), color(subTitle), fadeIn, stay, fadeOut);
    }

    public static void message(@NonNull Player player, @NonNull Object message) {
        player.sendMessage(Message.PREFIX + color(message));
    }

    public static void sound(@NonNull Player player, @NonNull Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, SoundCategory.RECORDS, 1.0f, pitch);
    }

    @NonNull
    public static String color(@NonNull Object object) {
        return ChatColor.translateAlternateColorCodes('&', String.valueOf(object));
    }

}
