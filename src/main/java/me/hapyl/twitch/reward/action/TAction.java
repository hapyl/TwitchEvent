package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.TwitchUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public abstract class TAction {

    protected final String name;
    protected final ExpectedParameterKeySet exceptedParameters;

    protected TAction(String name) {
        this.name = name;
        this.exceptedParameters = new ExpectedParameterKeySet();
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public ExpectedParameterKeySet getExceptedParameters() {
        return exceptedParameters;
    }

    public abstract boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params);

    // Static helpers
    protected static void forEach(@NonNull Consumer<Player> consumer) {
        Bukkit.getOnlinePlayers().forEach(consumer); // filtering survival players makes it painful to debug :/
    }
}
