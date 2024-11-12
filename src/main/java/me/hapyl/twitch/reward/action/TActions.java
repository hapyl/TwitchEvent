package me.hapyl.twitch.reward.action;

import com.google.common.collect.Maps;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class TActions {

    public static final TAction SPAWN_ENTITY;
    public static final TAction CLEAR_INVENTORY;

    private static final Map<String, TAction> ACTIONS;

    static {
        ACTIONS = Maps.newHashMap();

        SPAWN_ENTITY = register("spawn_entity", SpawnEntityTAction::new);
        CLEAR_INVENTORY = register("clear_inventory", ClearInventoryTAction::new);
    }

    @Nullable
    public static TAction byName(@NonNull String name) {
        return ACTIONS.get(name.toLowerCase());
    }

    private static <T extends TAction> T register(@NonNull String name, @NonNull Function<String, T> fn) {
        final T action = fn.apply(name);
        ACTIONS.put(name, action);

        return action;
    }

}
