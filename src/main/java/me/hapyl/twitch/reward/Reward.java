package me.hapyl.twitch.reward;

import me.hapyl.twitch.reward.action.TAction;
import me.hapyl.twitch.reward.action.param.ParameterList;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record Reward(@NonNull String name, @NonNull TAction action, @NonNull String message,
                     @NonNull String messageFailed, @NonNull ParameterList parameterList, @NonNull List<ItemStack> itemRewards) {


    @Override
    public String toString() {
        return this.name;
    }
}
