package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.util.Enums;
import me.hapyl.twitch.util.Strict;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.Random;

public class SpawnEntityTAction extends TAction {

    public SpawnEntityTAction(@NonNull String name) {
        super(name);

        this.exceptedParameters.except("entity_type");
        this.exceptedParameters.except("chance");
    }

    @Override
    public boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params) {
        final String entityTypeString = params.get("entity_type", ParameterTypeApplier.STRING);
        final EntityType entityType = Enums.byName(EntityType.class, entityTypeString);

        final Double chance = params.get("chance", ParameterTypeApplier.DOUBLE);

        if (entityType == null) {
            throw Strict.illegalArgument("Invalid entity: " + entityTypeString);
        }

        final Class<? extends Entity> entityClass = entityType.getEntityClass();

        if (entityClass == null) {
            throw Strict.illegalArgument("Entity " + entityTypeString + " cannot be spawned!");
        }

        if (chance < 0 || chance > 100) {
            throw Strict.illegalArgument("Chance must be between 0-100, '%s' isn't!".formatted(chance));
        }

        final double normalChance = chance / 100d;
        final double randomDouble = new Random().nextDouble();

        if (randomDouble >= normalChance) {
            return false;
        }

        // Perform
        forEach(player -> {
            final Location location = player.getLocation();
            final World world = player.getWorld();

            final Entity entity = world.spawn(location, entityClass, self -> {
                self.setCustomName(user.getDisplayName());
                self.setCustomNameVisible(true);
            });

            System.out.println(entity);
        });

        return true;
    }
}
