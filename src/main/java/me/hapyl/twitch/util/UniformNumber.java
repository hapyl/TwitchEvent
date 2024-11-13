package me.hapyl.twitch.util;

import me.hapyl.twitch.Main;
import org.bukkit.util.NumberConversions;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public record UniformNumber(double min, double max) {

    @NonNull
    public static UniformNumber of(double d) {
        return new UniformNumber(d, d);
    }

    public int asInt() {
        return (int) getValue();
    }

    public double asDouble() {
        return getValue();
    }

    private double getValue() {
        if (min == max) {
            return min;
        }

        return Main.RANDOM.nextDouble(min, max + 1);
    }

    @NonNull
    public static Function<String, UniformNumber> asGetter(@NonNull Function<UniformNumber, UniformNumber> fn) {
        return string -> {
            double min;
            double max;

            if (string.contains("..")) {
                final String[] range = string.split("\\.\\.");

                min = NumberConversions.toDouble(range[0]);
                max = NumberConversions.toDouble(range[1]);
            }
            else {
                min = NumberConversions.toDouble(string);
                max = min;
            }

            if (min > max) {
                throw new IllegalArgumentException("'min' cannot be greater than 'max'!");
            }

            return fn.apply(new UniformNumber(min, max));
        };
    }
}
