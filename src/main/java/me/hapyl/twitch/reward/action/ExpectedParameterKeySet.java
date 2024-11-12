package me.hapyl.twitch.reward.action;

import com.google.common.collect.Maps;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ExpectedParameterKeySet {

    private final Map<String, String> keys;

    public ExpectedParameterKeySet() {
        this.keys = Maps.newHashMap();
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @NonNull
    public Map<String, String> getKeys() {
        return keys;
    }

    protected void except(@NonNull String key, @NonNull String description) {
        this.keys.put(key, description);
    }

    protected void except(@NonNull String key) {
        except(key, "Anonymous parameter");
    }
}
