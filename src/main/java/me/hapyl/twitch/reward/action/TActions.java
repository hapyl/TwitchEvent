package me.hapyl.twitch.reward.action;

import com.google.common.collect.Maps;
import me.hapyl.twitch.IScheduledTicking;
import me.hapyl.twitch.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class TActions {

    public static final TAction SPAWN_ENTITY;
    public static final TAction CLEAR_INVENTORY;
    public static final TAction ADD_EFFECT;
    public static final TAction DROP_HELD_ITEM;
    public static final TAction TELEPORT_NEARBY;
    public static final TAction WATER_BUCKET_CHALLENGE;
    public static final TAction BLOCK_ACTION;
    public static final TAction CREEPER_SWEEPER;

    private static final Map<String, TAction> ACTIONS;

    static {
        ACTIONS = Maps.newHashMap();

        SPAWN_ENTITY = register("spawn_entity", SpawnEntityTAction::new);
        CLEAR_INVENTORY = register("clear_inventory", ClearInventoryTAction::new);
        ADD_EFFECT = register("add_effect", AddEffectTAction::new);
        DROP_HELD_ITEM = register("drop_held_item", DropHeldItemTAction::new);
        TELEPORT_NEARBY = register("teleport_nearby", TeleportNearbyTAction::new);
        WATER_BUCKET_CHALLENGE = register("water_bucket_challenge", WaterBucketChallengeTAction::new);
        BLOCK_ACTION = register("block_action", BlockActionTAction::new);
        CREEPER_SWEEPER = register("creeper_sweeper", CreeperSweeperTAction::new);
    }

    @Nullable
    public static TAction byName(@NonNull String name) {
        return ACTIONS.get(name.toLowerCase());
    }

    private static <T extends TAction> T register(@NonNull String name, @NonNull Function<String, T> fn) {
        final T action = fn.apply(name);
        ACTIONS.put(name, action);

        // Register listener
        final Main plugin = Main.getPlugin();

        if (action instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }

        // Add ticker
        if (action instanceof IScheduledTicking ticking) {
            plugin.runnable.registerTicking(ticking);
        }

        return action;
    }

}
