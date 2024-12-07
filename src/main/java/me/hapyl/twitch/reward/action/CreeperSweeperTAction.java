package me.hapyl.twitch.reward.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.intellij.lang.annotations.MagicConstant;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CreeperSweeperTAction extends TCompletableAction implements Listener {

    private static final int[] CREEPER_COUNT = { 6, 8, 12 };
    private static final int[] DURATION = { 60, 50, 40 };
    private static final double[] DISTANCE = { 2.0d, 2.5d, 3.0d };

    private static final int MAX_ROUNDS = CREEPER_COUNT.length;

    @MagicConstant private static final String MAGIC_CREEPER_TAG = "IsGameCreeper";

    private final Map<Player, CreeperSweeper> creeperSweeperMap;

    public CreeperSweeperTAction(String name) {
        super(name);

        this.creeperSweeperMap = Maps.newHashMap();
    }

    @EventHandler
    public void handleEntityTargetEvent(EntityTargetEvent ev) {
        final Entity target = ev.getTarget();

        if (!(target instanceof Player player)) {
            return;
        }

        if (creeperSweeperMap.containsKey(player)) {
            ev.setCancelled(true);
            ev.setTarget(null);
        }
    }

    @EventHandler
    public void handleEntityDamageEvent(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();

        if (!(entity instanceof Creeper creeper)) {
            return;
        }

        // We have to cancel damage other than player attacks
        if (!creeper.getScoreboardTags().contains(MAGIC_CREEPER_TAG)) {
            return;
        }

        final EntityDamageEvent.DamageCause cause = ev.getCause();

        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent ev) {
        final Entity entity = ev.getEntity();
        final Entity damager = ev.getDamager();

        if (!(entity instanceof Creeper creeper)) {
            return;
        }

        if (!(damager instanceof Player player)) {
            return;
        }

        for (CreeperSweeper sweeper : creeperSweeperMap.values()) {
            if (sweeper.player != player) {
                continue;
            }

            final Boolean isCorrectCreeper = sweeper.round.creepers.get(creeper);

            // Not our creeper
            if (isCorrectCreeper == null) {
                continue;
            }

            if (isCorrectCreeper) {
                sweeper.roundSuccess();
            }
            else {
                sweeper.roundFailed("Не тот Крипер!");
            }
        }
    }

    @Override
    public boolean perform(@NonNull Player player, @NonNull TwitchUser user, @NonNull ParameterList params) {
        final CreeperSweeper previous = creeperSweeperMap.put(player, new CreeperSweeper(player));

        if (previous != null) {
            previous.cancel();
        }

        return true;
    }

    private class CreeperSweeper {
        private final Player player;

        private CreeperSweeperRound round;
        private int currentRound = 0;

        private CreeperSweeper(Player player) {
            this.player = player;
            this.nextRound();

            // Lose aggro because mobs are annoying
            final Location location = player.getLocation();

            player.getWorld()
                    .getNearbyEntities(location, 10, 10, 10)
                    .stream()
                    .filter(entity -> entity instanceof Mob)
                    .map(entity -> (Mob) entity)
                    .forEach(entity -> {
                        if (entity.getTarget() == player) {
                            entity.setTarget(null);
                        }
                    });
        }

        @SuppressWarnings("UnstableApiUsage")
        public void roundFailed(@NonNull String reason) {
            cancel();
            creeperSweeperMap.remove(player);

            final Location location = player.getLocation();

            Message.error("Creeper Sweeper Failed! {%s}".formatted(reason));

            player.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);
            player.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 0.75f);
            player.damage(1000, DamageSource.builder(DamageType.EXPLOSION).build());
        }

        public void roundSuccess() {
            cancel();
            nextRound();
        }

        public void cancel() {
            round.cancel();

            player.setWalkSpeed(0.2f);
            player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.42d);
        }

        private void nextRound() {
            currentRound++;

            if (currentRound > MAX_ROUNDS) {
                cancel();
                creeperSweeperMap.remove(player);
                completeAction(player);

                return;
            }

            round = new CreeperSweeperRound(this, CREEPER_COUNT[currentRound - 1], DURATION[currentRound - 1]);
        }

    }

    private class CreeperSweeperRound extends BukkitRunnable {
        private final CreeperSweeper sweeper;
        private final Map<Creeper, Boolean> creepers;

        private int duration;

        private CreeperSweeperRound(CreeperSweeper sweeper, int creeperCount, int duration) {
            this.sweeper = sweeper;
            this.creepers = Maps.newHashMap();
            this.duration = duration;

            createCreepers(creeperCount);
            runTaskTimer(Main.getPlugin(), 0, 1);
        }

        @Override
        public synchronized void cancel() {
            try {
                super.cancel();
            } catch (IllegalStateException ignored) {
            }

            creepers.keySet().forEach(Entity::remove);
            creepers.clear();
        }

        @Override
        public void run() {
            if (duration-- <= 0) {
                sweeper.roundFailed("Время вышло!");
                return;
            }

            // Set attribute here because I'm lazy
            sweeper.player.setWalkSpeed(0.0f); // setWalkSpeed() does not fuck with FOV
            sweeper.player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0d);

            final int threshold = 20;
            final NamedTextColor color = duration % threshold < (threshold / 2) ? NamedTextColor.RED : NamedTextColor.DARK_RED;

            final Title title = Title.title(
                    Component.empty(),
                    Component.text("%.1fs".formatted(duration / 20d), color, TextDecoration.BOLD),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)
            );

            sweeper.player.showTitle(title);
        }

        private void createCreepers(int creeperCount) {
            final World world = sweeper.player.getWorld();
            final Location location = sweeper.player.getLocation();
            location.setPitch(0.0f);

            final double spread = Math.PI * 2 / creeperCount;
            final int correctCreeper = ThreadLocalRandom.current().nextInt(creeperCount);
            final double distance = DISTANCE[sweeper.currentRound - 1];

            for (int i = 0; i < creeperCount; i++) {
                final boolean isCorrectCreeper = correctCreeper == i;

                // Break blocks first
                getVisionBlockingBlocks(location, (int) (distance + 1)).forEach(sweeper.player::breakBlock);

                final double x = Math.sin(i * spread) * distance;
                final double z = Math.cos(i * spread) * distance;

                location.add(x, 0, z);

                // Direction
                final Location playerLocation = sweeper.player.getLocation();
                location.setDirection(playerLocation.toVector().subtract(location.toVector()).multiply(-1));

                final Creeper creeper = world.spawn(location, Creeper.class, self -> {
                    self.setAI(false);
                    self.setPowered(isCorrectCreeper);
                    self.addScoreboardTag(MAGIC_CREEPER_TAG);
                });

                location.setDirection(playerLocation.toVector().subtract(location.toVector()));
                creeper.teleport(location);

                creepers.put(creeper, isCorrectCreeper);
                location.subtract(x, 0, z);
            }
        }

        private List<Block> getVisionBlockingBlocks(Location location, int radius) {
            final World world = location.getWorld();
            final List<Block> blocks = Lists.newArrayList();

            final int tX = location.getBlockX();
            final int tY = location.getBlockY();
            final int tZ = location.getBlockZ();

            for (int x = tX - radius; x <= tX + radius; x++) {
                for (int z = tZ - radius; z <= tZ + radius; z++) {
                    if ((tX - x) * (tX - x) + (tZ - z) * (tZ - z) <= ((radius * radius) + 2)) {
                        final Block block = world.getBlockAt(x, tY, z);
                        final Block blockAbove = world.getBlockAt(x, tY + 1, z);

                        if (!block.isEmpty()) {
                            blocks.add(block);
                        }

                        if (!blockAbove.isEmpty()) {
                            blocks.add(blockAbove);
                        }
                    }
                }
            }

            return blocks;
        }

    }
}



