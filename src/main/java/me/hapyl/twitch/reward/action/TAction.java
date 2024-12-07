package me.hapyl.twitch.reward.action;

import com.google.common.collect.Sets;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.Reward;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.StaticFormat;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Set;

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

    public abstract boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params);

    public void validateParameters(@NonNull ParameterList parameterList) {
        if (this.parameters.isEmpty() && parameterList.isEmpty()) {
            return;
        }

        for (ParameterGetter<?> getter : this.parameters) {
            parameterList.get(getter); // Just call get once it'll validate it
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void onSuccess(@NonNull Player player, @NonNull TwitchUser user, @NonNull Reward reward) {
        Message.success(StaticFormat.format(reward.message(), user, player));
    }

    @OverridingMethodsMustInvokeSuper
    public void onFail(@NonNull Player player, @NonNull TwitchUser user, @NonNull Reward reward) {
        Message.error(StaticFormat.format(reward.messageFailed(), user, player));
    }

    protected void registerParameter(@NonNull ParameterGetter<?> getter) {
        this.parameters.add(getter);
    }

}
