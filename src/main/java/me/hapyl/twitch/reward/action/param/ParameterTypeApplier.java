package me.hapyl.twitch.reward.action.param;

import me.hapyl.twitch.util.Enums;
import org.bukkit.entity.EntityType;
import org.bukkit.util.NumberConversions;
import org.jspecify.annotations.NonNull;

public interface ParameterTypeApplier<T> {

    ParameterTypeApplier<String> STRING = t -> t;
    ParameterTypeApplier<Integer> INTEGER = NumberConversions::toInt;
    ParameterTypeApplier<Double> DOUBLE = NumberConversions::toDouble;

    ParameterTypeApplier<EntityType> ENTITY_TYPE = t -> {
        final EntityType entityType = Enums.byName(EntityType.class, t);

        if (entityType != null) {
            return entityType;
        }

        throw new IllegalArgumentException("Illegal entity type: " + t);
    };

    @NonNull
    T apply(@NonNull String string) throws IllegalArgumentException;

}
