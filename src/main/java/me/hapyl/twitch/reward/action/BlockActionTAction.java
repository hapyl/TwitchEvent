package me.hapyl.twitch.reward.action;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.common.collect.Maps;
import me.hapyl.twitch.IScheduledTicking;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Enums;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.NumberConversions;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;
import java.util.Map;

public class BlockActionTAction extends TAction implements IScheduledTicking, Listener {

    private final ParameterGetter<Action> action = ParameterGetter.of("action", string -> {
        final Action action = Enums.byName(Action.class, string);

        if (action == null) {
            throw new IllegalArgumentException("Invalid action: %s!".formatted(string));
        }

        return action;
    });

    private final ParameterGetter<Integer> duration = ParameterGetter.of("duration", string -> Math.max(1, NumberConversions.toInt(string)));

    private final Map<Action, ActionBlock> blockedActions;
    private final int delay = 100;

    public BlockActionTAction(String name) {
        super(name);

        registerParameter(this.action);
        registerParameter(this.duration);

        this.blockedActions = Maps.newConcurrentMap();
    }

    @Override
    public void tick() {
        final Iterator<ActionBlock> iterator = this.blockedActions.values().iterator();

        while (iterator.hasNext()) {
            final ActionBlock block = iterator.next();
            final Action action = block.action;

            block.duration--;

            // Finished
            if (block.duration <= 0) {
                iterator.remove();

                notifyAction(action, false);
            }
        }
    }

    public void notifyAction(@NonNull Action action, boolean blocked) {
        if (blocked) {
            Message.error("Действие {%s} запрещено!".formatted(action.name));
        }
        else {
            Message.success("Действие {%s} разрешено!".formatted(action.name));
        }
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        final Action action = params.get(this.action);
        final int duration = params.get(this.duration);

        if (blockedActions.containsKey(action)) {
            Message.info("Длительность действия {%s} увеличена!".formatted(action.name));
            blockedActions.get(action).duration += duration;
        }
        else {
            Message.info("Действие {%s} будет запрещено через {%s}c!".formatted(action.name, delay / 20));

            Tasks.later(() -> {
                blockedActions.put(action, new ActionBlock(action, duration));
                notifyAction(action, true);
            }, delay);
        }

        return true;
    }

    @EventHandler
    public void handleCraft(CraftItemEvent ev) {
        if (!(ev.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!isBlocked(Action.CRAFT)) {
            return;
        }

        punish(player, Action.CRAFT);
    }

    @EventHandler
    public void handleJump(PlayerJumpEvent ev) {
        if (!isBlocked(Action.JUMP)) {
            return;
        }

        punish(ev.getPlayer(), Action.JUMP);
    }

    @EventHandler
    public void handleSneak(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();

        // Don't care about unsneaking
        if (!ev.isSneaking() || !isBlocked(Action.SNEAK)) {
            return;
        }

        punish(player, Action.SNEAK);
    }

    @EventHandler
    public void handleSprint(PlayerToggleSprintEvent ev) {
        final Player player = ev.getPlayer();

        if (!ev.isSprinting() || !isBlocked(Action.SPRINT)) {
            return;
        }

        punish(player, Action.SPRINT);
    }

    public boolean isBlocked(@NonNull Action action) {
        return blockedActions.containsKey(action);
    }

    private void punish(@NonNull Player player, @NonNull Action action) {
        player.damage(player.getHealth() + 1);

        Message.error("{%s} забыл, чтобы действие {%s} запрещено!".formatted(player.getName(), action.name));
    }

    public enum Action {
        CRAFT("Крафт"),
        JUMP("Прыжок"),
        SNEAK("Присед"),
        SPRINT("Бег");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private static class ActionBlock {
        private final Action action;
        private int duration;

        private ActionBlock(Action action, int duration) {
            this.action = action;
            this.duration = duration;
        }
    }

}
