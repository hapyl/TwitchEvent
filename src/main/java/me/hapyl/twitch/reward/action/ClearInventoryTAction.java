package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterGetters;
import me.hapyl.twitch.reward.action.param.ParameterList;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class ClearInventoryTAction extends TAction {

    private final ParameterGetter<Boolean> chance = ParameterGetters.CHANCE;

    protected ClearInventoryTAction(String name) {
        super(name);

        registerParameter(this.chance);
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        if (!params.get(this.chance)) {
            return false;
        }

        player.getInventory().clear();
        return true;
    }
}
