package me.hapyl.twitch.reward.action.param;

import me.hapyl.twitch.IllegalParameterException;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ParameterList {

    public static final ParameterList EMPTY = new ParameterList(Map.of());

    private final Map<String, String> parameters;

    public ParameterList(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @NonNull
    public <T> T get(@NonNull ParameterGetter<T> getter) {
        final String key = getter.getKey();
        final String object = parameters.get(key);

        // Handle default value
        if (object == null) {
            final T value = getter.defaultValue();

            if (value == null) {
                throw new IllegalParameterException(key, "Parameter is missing!");
            }

            return value;
        }

        try {
            return getter.get(object);
        } catch (Exception e) {
            throw new IllegalParameterException(key, e.getMessage());
        }
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }
}
