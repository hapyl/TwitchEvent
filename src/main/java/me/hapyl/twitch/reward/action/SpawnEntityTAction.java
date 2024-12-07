package me.hapyl.twitch.reward.action;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hapyl.twitch.IllegalParameterException;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterGetters;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Enums;
import me.hapyl.twitch.util.UniformNumber;
import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;

public class SpawnEntityTAction extends TAction {

    private final ParameterGetter<EntityType> entityType = new ParameterGetter<>("entity_type") {
        @Override
        @NonNull
        public EntityType get(@NonNull String string) throws IllegalParameterException {
            final EntityType entityType = Enums.byName(EntityType.class, string);

            if (entityType == null) {
                throw new IllegalArgumentException("Invalid entity!");
            }

            // Check if the entity can be spawned
            if (entityType.getEntityClass() == null || !entityType.isSpawnable()) {
                throw new IllegalArgumentException("Entity is not spawnable!");
            }

            return entityType;
        }
    };

    private final ParameterGetter<Boolean> chance = ParameterGetters.CHANCE;

    private final ParameterGetter<UniformNumber> amount = ParameterGetter.ofDefault("amount", UniformNumber.asGetter(uniform -> {
        if (uniform.min() < 1) {
            throw new IllegalArgumentException("'min' cannot be lower than '1'!");
        }

        return uniform;
    }), UniformNumber.of(1));

    private final ParameterGetter<CompoundTag> nbt = ParameterGetter.ofDefault("nbt", string -> {
        try {
            return TagParser.parseTag(string);
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("Invalid nbt! " + e.getMessage());
        }
    }, new CompoundTag());

    public SpawnEntityTAction(@NonNull String name) {
        super(name);

        registerParameter(this.entityType);
        registerParameter(this.chance);
        registerParameter(this.amount);
        registerParameter(this.nbt);
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        final EntityType entityType = params.get(this.entityType);
        final boolean chance = params.get(this.chance);
        final int amount = params.get(this.amount).asInt();
        final CompoundTag nbt = params.get(this.nbt);

        if (!chance) {
            return false;
        }

        assert entityType.getEntityClass() != null : "Entity class is null somehow!";

        // Perform
        final Location location = player.getLocation();
        final World world = player.getWorld();

        for (int i = 0; i < amount; i++) {
            final Entity entity = world.spawn(location, entityType.getEntityClass(), self -> {
                self.customName(Component.text(user.getDisplayName()));
                self.setCustomNameVisible(true);

                // Make sure to always aggro the entity
                if (self instanceof Monster monster) {
                    monster.setTarget(player);
                }
            });

            // Apply nbt
            if (!nbt.isEmpty()) {
                try {
                    // Have to use fucking reflection because spigot is being annoying
                    final Method method = entity.getClass().getMethod("getHandle");
                    final net.minecraft.world.entity.Entity mcEntity = (net.minecraft.world.entity.Entity) method.invoke(entity);

                    final CompoundTag newNbt = new CompoundTag();
                    mcEntity.save(newNbt);

                    newNbt.merge(nbt);
                    mcEntity.load(newNbt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
