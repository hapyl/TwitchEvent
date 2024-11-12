package me.hapyl.twitch.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public final class Enums {

    @Nullable
    public static <E extends Enum<E>> E byName(@NonNull Class<E> enumClass, @NotNull String name) {
        try {
            return Enum.valueOf(enumClass, name);
        } catch (Exception e) {
            return null;
        }
    }

}
