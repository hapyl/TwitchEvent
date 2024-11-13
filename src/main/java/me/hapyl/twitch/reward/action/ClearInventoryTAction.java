package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterGetters;
import me.hapyl.twitch.reward.action.param.ParameterList;
import org.jspecify.annotations.NonNull;

public class ClearInventoryTAction extends TAction {

    private final ParameterGetter<Boolean> chance = ParameterGetters.CHANCE;

    protected ClearInventoryTAction(String name) {
        super(name);

        registerParameter(this.chance);
    }

    @Override
    public boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params) {
        if (!params.get(this.chance)) {
            return false;
        }

        forEach(player -> {
            player.getInventory().clear();
        });

        return true;
    }
}
