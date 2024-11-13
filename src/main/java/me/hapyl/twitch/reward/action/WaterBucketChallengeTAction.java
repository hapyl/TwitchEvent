package me.hapyl.twitch.reward.action;

import com.google.common.collect.Maps;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.PlayerUtil;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.Tasks;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class WaterBucketChallengeTAction extends TAction implements Listener {

    private static final int HEIGHT = 50;
    private static final int HEIGHT_NETHER = 30;
    private static final int DELAY_SEC = 3;

    private final Map<Player, WaterBucketChallenge> challenges;

    public WaterBucketChallengeTAction(String name) {
        super(name);

        this.challenges = Maps.newHashMap();
    }

    @EventHandler
    public void handleEntityDropItemEvent(PlayerDropItemEvent ev) {
        final Entity player = ev.getPlayer();

        if (this.challenges.containsKey(player)) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void handleEntityDeathEvent(EntityDeathEvent ev) {
        final LivingEntity entity = ev.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        final WaterBucketChallenge challenge = this.challenges.remove(player);

        if (challenge != null) {
            challenge.onDeath(ev);
        }
    }

    @Override
    public boolean perform(@NonNull TwitchUser user, @NonNull ParameterList params) {
        affectPlayers(player -> {
            Message.info("Готовься {%s}, Water Bucket Challenge через {%s}с!".formatted(player.getName(), DELAY_SEC));
            PlayerUtil.playSound(player, Sound.AMBIENT_UNDERWATER_EXIT, 1.0f);

            new BukkitRunnable() {
                private int delay = DELAY_SEC;

                @Override
                public void run() {
                    if (delay <= 0) {
                        final WaterBucketChallenge previousChallenge = challenges.remove(player);

                        if (previousChallenge != null) {
                            previousChallenge.onSuccess();
                        }

                        // Don't cheat!
                        player.closeInventory();

                        PlayerUtil.sendTitle(player, "&a&lGO!", "", 10, 10, 10);
                        PlayerUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f);

                        challenges.put(player, new WaterBucketChallenge(player));
                        cancel();
                    }
                    else {
                        final String color = delay == 3 ? "&4&l"
                                : delay == 2 ? "&c&l"
                                : delay == 1 ? "&e&l"
                                : "&a&l";

                        PlayerUtil.sendTitle(player,
                                color + delay,
                                "",
                                0, 20, 10
                        );

                        PlayerUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f + (1.0f * delay / DELAY_SEC));
                        PlayerUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.75f + (0.75f * delay / DELAY_SEC));
                    }

                    --delay;
                }
            }.runTaskTimer(Main.getPlugin(), 20, 20);
        });

        return true;
    }

    private class WaterBucketChallenge extends BukkitRunnable {

        private final Player player;
        private final World world;
        private final Location location;
        private final PlayerInventory inventory;
        private final ItemStack[] contents;
        private final boolean isInNether;

        private final ItemStack waterBucketItem;

        private WaterBucketChallenge(Player player) {
            this.player = player;
            this.world = player.getWorld();
            this.location = player.getLocation();
            this.inventory = player.getInventory();
            this.contents = inventory.getContents();
            this.isInNether = world.getEnvironment() == World.Environment.NETHER;

            this.waterBucketItem = new ItemStack(isInNether ? Material.POWDER_SNOW_BUCKET : Material.WATER_BUCKET);

            // Keep last
            this.doTheChallenge();
        }

        public void onDeath(@NonNull EntityDeathEvent ev) {
            cancel();

            // Steal the item
            this.inventory.removeItem(this.waterBucketItem);

            ev.getDrops().clear();

            // Drop at initial location
            final int droppedExp = ev.getDroppedExp();

            // Clear event drops
            ev.setDroppedExp(0);

            // Drop at initial location
            for (ItemStack content : this.contents) {
                if (content != null) {
                    this.world.dropItemNaturally(this.location, content);
                }
            }

            // Drop exp as well
            this.world.spawn(this.location, ExperienceOrb.class, self -> {
                self.setExperience(droppedExp);
            });
        }

        public void onSuccess() {
            this.player.teleport(this.location);

            this.inventory.remove(this.waterBucketItem);
            this.inventory.setContents(this.contents);

            // Only remove after it's we're teleported
            WaterBucketChallengeTAction.this.challenges.remove(this.player, this);
        }

        @Override
        public void run() {
            if (!this.player.isOnGround() && !this.player.isInWater() && !this.player.isInPowderedSnow()) {
                return;
            }

            cancel();

            final Location currentLocation = this.player.getLocation();

            clearWaterAndPowderSnow(currentLocation);

            // Fake it till you make it
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 10, 255, false, false, false));

            // Fx
            this.player.playSound(currentLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            // Return player to the initial location
            Tasks.later(this::onSuccess, 20);
        }

        private void clearWaterAndPowderSnow(Location location) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; z++) {
                        location.add(x, 0, z);

                        final Material type = location.getBlock().getType();

                        if (type == Material.WATER || type == Material.POWDER_SNOW) {
                            location.getBlock().setType(Material.AIR);
                        }

                        location.subtract(x, 0, z);
                    }
                }
            }
        }

        private void doTheChallenge() {
            this.inventory.clear();

            final int randomSlot = Main.RANDOM.nextInt(9, 35);

            // Give the item
            this.inventory.setItem(randomSlot, this.waterBucketItem);

            // Teleport to the top of the world
            final Location location = this.location.clone();
            location.setY(world.getHighestBlockYAt(location));
            location.setY(location.getY() + (isInNether ? HEIGHT_NETHER : HEIGHT));

            player.teleport(location);

            runTaskTimer(Main.getPlugin(), 10, 1); // wait 10 ticks for teleport to not be on the ground
        }
    }
}
