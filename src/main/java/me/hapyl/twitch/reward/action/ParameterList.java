package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.util.Strict;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ParameterList {

    public static final ParameterList EMPTY = new ParameterList(Map.of());

    private final Map<String, String> parameters;

    public ParameterList(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public <T> T get(@NonNull String key, @NonNull ParameterTypeApplier<T> applier) {
        final String object = parameters.get(key);

        if (object == null) {
            throw Strict.exception(new IllegalArgumentException("Parameter '%s' is missing!".formatted(key)));
        }

        return applier.apply(object);
    }

}
