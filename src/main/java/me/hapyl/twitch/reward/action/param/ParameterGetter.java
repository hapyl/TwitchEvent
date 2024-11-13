package me.hapyl.twitch.reward.action.param;

import me.hapyl.twitch.IllegalParameterException;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.util.UniformNumber;
import org.bukkit.util.NumberConversions;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public abstract class ParameterGetter<T> {

    private final String key;

    public ParameterGetter(String key) {
        this.key = key;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public abstract T get(@NonNull String string) throws IllegalParameterException;

    @Nullable
    public T defaultValue() {
        return null;
    }

    @NonNull
    public static <T> ParameterGetter<T> of(@NonNull String key, @NonNull Function<String, T> fn) {
        return ofDefault(key, fn, null);
    }

    @NonNull
    public static <T> ParameterGetter<T> ofDefault(@NonNull String key, @NonNull Function<String, T> fn, @Nullable T defaultValue) {
        return new ParameterGetter<>(key) {
            @Override
            @NonNull
            public T get(@NonNull String string) throws IllegalParameterException {
                return fn.apply(string);
            }

            @Override
            public @Nullable T defaultValue() {
                return defaultValue;
            }
        };
    }

}
