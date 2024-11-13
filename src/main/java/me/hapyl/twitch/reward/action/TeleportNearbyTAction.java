package me.hapyl.twitch.reward.action;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Message;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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

        affectPlayers(player -> {
            final Location location = getRandomLocation(player, radiusX, radiusY, radiusZ, 10);

            player.teleport(location);
        });

        return true;
    }

    private Location getRandomLocation(Player player, double x, double y, double z, int limit) {
        final World world = player.getWorld();
        final Location location = player.getLocation();

        if (limit <= 0) {
            Message.info("Не получилось найти куда телепортировать игрока!");
            return location;
        }

        location.add(
                Main.RANDOM.nextBoolean() ? x : -x,
                Main.RANDOM.nextBoolean() ? y : -y,
                Main.RANDOM.nextBoolean() ? z : -z
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

        if (isLocationSafe(location)) {
            return location;
        }

        return getRandomLocation(player, x, y, z, --limit);
    }

    private boolean isLocationSafe(Location location) {
        final World world = location.getWorld();
        final double y = location.getY();

        if (world.getEnvironment() == World.Environment.NETHER && y >= 128) {
            return false;
        }

        if (y <= world.getMinHeight()) {
            return false;
        }

        final Block block = location.getBlock();

        if (block.isLiquid()) {
            return false;
        }

        // Must be 2 blocks
        if (block.isPassable() && block.getRelative(BlockFace.UP).isPassable()) {
            return true;
        }

        return false;
    }
}
