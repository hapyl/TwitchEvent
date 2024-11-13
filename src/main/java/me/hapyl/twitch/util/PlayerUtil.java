package me.hapyl.twitch.util;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public final class PlayerUtil {

    public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(color(title), color(subTitle), fadeIn, stay, fadeOut);
    }

    public static void playSound(Player player, Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, SoundCategory.RECORDS, 1.0f, pitch);
    }

    public static String color(Object object) {
        return ChatColor.translateAlternateColorCodes('&', String.valueOf(object));
    }

}
