package me.hapyl.twitch.reward.action.param;

import me.hapyl.twitch.Main;
import org.bukkit.util.NumberConversions;

public interface ParameterGetters {

    ParameterGetter<Boolean> CHANCE = ParameterGetter.of(
            "chance",
            string -> {
                final double chance = Math.clamp(NumberConversions.toDouble(string), 0, 100) / 100;

                return Main.RANDOM.nextDouble() < chance;
            }
    );

}
