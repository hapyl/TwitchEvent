package me.hapyl.twitch.reward.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class TAction {

    protected final String name;
    protected final Set<ParameterGetter<?>> parameters;

    public TAction(String name) {
        this.name = name;
        this.parameters = Sets.newHashSet();
    }

    @NonNull
    public String getName() {
        return name;
    }

    public abstract boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params);

    public void validateParameters(@NonNull ParameterList parameterList) {
        if (this.parameters.isEmpty() && parameterList.isEmpty()) {
            return;
        }

        for (ParameterGetter<?> getter : this.parameters) {
            parameterList.get(getter); // Just call get once it'll validate it
        }
    }

    protected void registerParameter(@NonNull ParameterGetter<?> getter) {
        this.parameters.add(getter);
    }

    // Static helpers
    protected static void forEach(@NonNull Consumer<Player> consumer) {
        final List<Player> onlinePlayers = Lists.newArrayList(Bukkit.getOnlinePlayers());
        final boolean isShared = Main.getPlugin().config.getYaml().getBoolean("shared_punishment");

        if (onlinePlayers.isEmpty()) {
            Message.error("Никто не онлайн!");
            return;
        }

        if (isShared) {
            onlinePlayers.forEach(consumer);
        }
        else {
            Collections.shuffle(onlinePlayers);
            consumer.accept(onlinePlayers.getFirst());
        }
    }
}
