package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterGetter;
import me.hapyl.twitch.reward.action.param.ParameterGetters;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;

public class DropHeldItemTAction extends TAction {

    private final ParameterGetter<Boolean> chance = ParameterGetters.CHANCE;

    public DropHeldItemTAction(String name) {
        super(name);

        registerParameter(this.chance);
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        if (!params.get(this.chance)) {
            return false;
        }

        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().multiply(1);
        location.add(vector);

        final Block block = location.getBlock();

        final BlockState state = block.getState();

        block.setType(Material.LAVA, false);

        Tasks.later(() -> player.dropItem(true), 2);
        Tasks.later(() -> state.update(true, false), 10);
        return true;
    }

}
