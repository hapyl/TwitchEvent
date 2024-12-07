package me.hapyl.twitch.reward.action;

import com.google.common.collect.Lists;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterList;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class AddEffectTAction extends TAction {

    private final ParameterGetter<List<PotionEffect>> effects = ParameterGetter.of("effects", string -> {
        final List<PotionEffect> effectList = Lists.newArrayList();

        final String[] splitEffects = string.split(",");
        for (String splitEffect : splitEffects) {
            final String[] splitEffectData = splitEffect.split(":");

            final PotionEffectType effectType = Registry.EFFECT.get(NamespacedKey.minecraft(splitEffectData[0]));
            final int amplifier = NumberConversions.toInt(splitEffectData[1]);
            final int duration = NumberConversions.toInt(splitEffectData[2].replace("s", ""));

            if (effectType == null) {
                throw new IllegalArgumentException("Invalid effect: '%s'!".formatted(splitEffectData[0]));
            }

            if (amplifier < 0) {
                throw new IllegalArgumentException("Amplifier cannot be negative!");
            }

            if (duration < 0) {
                throw new IllegalArgumentException("Duration cannot be negative!");
            }

            effectList.add(new PotionEffect(effectType, duration * 20, amplifier - 1));
        }

        return effectList;
    });

    private final ParameterGetter<Boolean> isRandom = ParameterGetter.of("is_random", string -> string.equalsIgnoreCase("true"));

    public AddEffectTAction(String name) {
        super(name);

        registerParameter(effects);
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        final List<PotionEffect> effects = params.get(this.effects);
        final boolean isRandom = params.get(this.isRandom);

        if (isRandom) {
            final PotionEffect randomEffect = effects.get(Main.RANDOM.nextInt(effects.size()));
            effects.retainAll(List.of(randomEffect));
        }

        effects.forEach(player::addPotionEffect);
        return true;
    }

}
