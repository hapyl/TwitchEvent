package me.hapyl.twitch.reward.action;

import com.google.common.collect.Lists;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TheFloorIsLavaTAction extends TAction {

    public TheFloorIsLavaTAction(String name) {
        super(name);
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        new TheFloorIsLava(player, player.getLocation());
        return true;
    }

    private final class TheFloorIsLava extends BukkitRunnable {

        private final Player player;
        private final Location centre;

        private final List<Block> affectedBlocks;

        private TheFloorIsLava(Player player, Location centre) {
            this.player = player;
            this.centre = centre;
            this.affectedBlocks = Lists.newArrayList();

            // Affect blocks
            final Location location = player.getLocation();
        }

        @Override
        public void run() {

        }
    }
}
