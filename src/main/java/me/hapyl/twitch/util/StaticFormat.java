package me.hapyl.twitch.util;

import com.google.common.collect.Sets;
import me.hapyl.twitch.TwitchUser;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.function.Function;

public final class StaticFormat<K> {
    private static final Set<StaticFormat<?>> formats = Sets.newHashSet();

    static {
        of("user", TwitchUser.class, TwitchUser::getDisplayName);
        of("player", Player.class, Player::getName);
    }

    private final String name;
    private final Class<K> clazz;
    private final Function<K, String> fn;

    private StaticFormat(String name, Class<K> clazz, Function<K, String> fn) {
        this.name = name;
        this.clazz = clazz;
        this.fn = fn;

        formats.add(this);
    }

    @NonNull
    public String format(@NonNull String string, @NonNull Object object) {
        if (!clazz.isInstance(object)) {
            return string;
        }

        return string.replace(name, fn.apply(clazz.cast(object)));
    }

    @NonNull
    public static String format(@NonNull String string, @NonNull Object... objects) {
        for (StaticFormat<?> format : formats) {
            for (@NonNull Object object : objects) {
                string = format.format(string, object);
            }
        }

        return string;
    }

    private static <K> void of(String name, Class<K> clazz, Function<K, String> fn) {
        new StaticFormat<>(name, clazz, fn);
    }

}
