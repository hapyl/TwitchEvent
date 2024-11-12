package me.hapyl.twitch;

import me.hapyl.twitch.command.TwitchCommand;
import me.hapyl.twitch.connection.EventSubWorker;
import me.hapyl.twitch.reward.RewardQueue;
import me.hapyl.twitch.reward.RewardRegistry;
import me.hapyl.twitch.util.Strict;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main main;

    public YamlConfig config;
    public Secret secret;
    public RewardRegistry registry;
    public RewardQueue queue;

    @Override
    public void onEnable() {
        main = this;

        // Load config
        this.config = new YamlConfig("config.yml");

        // Load secret
        this.secret = new Secret();

        // Setup worker
        final EventSubWorker worker = new EventSubWorker(this.secret);
        worker.establishConnection();

        // Create registry
        this.registry = new RewardRegistry();

        // Init queue
        restartQueue();

        // Register commands
        new TwitchCommand();
    }

    public void restartQueue() {
        if (this.queue != null) {
            this.queue.cancel();
        }

        final int redemptionRate = config.getYaml().getInt("redemption_rate") * 20; // convert to ticks

        if (redemptionRate <= 0) {
            throw Strict.illegalArgument("Redemption rate must be positive");
        }

        this.queue = new RewardQueue();
        this.queue.runTaskTimer(this, redemptionRate, redemptionRate);
    }

    @NotNull
    public static Main getPlugin() {
        return Objects.requireNonNull(main);
    }
}
