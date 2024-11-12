package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.util.Strict;
import org.jspecify.annotations.NonNull;

public class ClearInventoryTAction extends TAction {
    protected ClearInventoryTAction(String name) {
        super(name);

        exceptedParameters.except("chance");
    }

    @Override
    public boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params) {
        final Double chance = params.get("chance", ParameterTypeApplier.DOUBLE);

        if (chance < 0 || chance > 100) {
            throw Strict.illegalArgument("Chance must be between 0-100, '%s' isn't!".formatted(chance));
        }

        final double normalChance = chance / 100;

        if (Math.random() >= normalChance) {
            return false;
        }

        forEach(player -> {
            player.getInventory().clear();
        });

        return true;
    }
}
