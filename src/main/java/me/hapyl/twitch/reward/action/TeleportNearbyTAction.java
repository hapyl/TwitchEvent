package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jspecify.annotations.NonNull;

public class TeleportNearbyTAction extends TAction {

    private final double minRadius = 5;
    private final double maxRadius = 64;

    public TeleportNearbyTAction(String name) {
        super(name);
    }

    @Override
    public boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params) {
        final double radiusX = Main.RANDOM.nextDouble(minRadius, maxRadius);
        final double radiusY = Main.RANDOM.nextDouble(minRadius, maxRadius);
        final double radiusZ = Main.RANDOM.nextDouble(minRadius, maxRadius);

        forEach(player -> {
            final World world = player.getWorld();
            final Location location = player.getLocation();

            location.add(
                    Main.RANDOM.nextBoolean() ? radiusX : -radiusX,
                    Main.RANDOM.nextBoolean() ? radiusY : -radiusY,
                    Main.RANDOM.nextBoolean() ? radiusZ : -radiusZ
            );

            // Fix Y
            Block block = location.getBlock();

            // Fix up
            while (!block.isPassable() && block.getY() < world.getMaxHeight()) {
                block = block.getRelative(BlockFace.UP);
            }

            // Fix down
            while (!block.getRelative(BlockFace.DOWN).isSolid() && block.getY() > world.getMinHeight()) {
                block = block.getRelative(BlockFace.DOWN);
            }

            location.setY(block.getY());
            player.teleport(location);
        });

        return true;
    }
}
