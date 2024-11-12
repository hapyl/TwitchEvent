package me.hapyl.twitch.reward;

import me.hapyl.twitch.reward.action.ParameterList;
import me.hapyl.twitch.reward.action.TAction;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class Reward {

    private final String name;
    private final TAction action;
    private final String message;
    private final String messageFailed;
    @Nullable
    private final ParameterList parameterList;

    public Reward(String name, TAction action, String message, String messageFailed, @Nullable ParameterList parameterList) {
        this.name = name;
        this.action = action;
        this.message = message;
        this.messageFailed = messageFailed;
        this.parameterList = parameterList;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public TAction getAction() {
        return action;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @NonNull
    public String getMessageFailed() {
        return messageFailed;
    }

    public @Nullable ParameterList getParameterList() {
        return parameterList;
    }
}
