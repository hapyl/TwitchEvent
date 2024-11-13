package me.hapyl.twitch;

import me.hapyl.twitch.command.TwitchCommand;
import me.hapyl.twitch.connection.EventSubWorker;
import me.hapyl.twitch.reward.RewardQueue;
import me.hapyl.twitch.reward.RewardRegistry;
import me.hapyl.twitch.util.Strict;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public final class Main extends JavaPlugin {

    public static final Random RANDOM = new Random();

    private static Main main;

    public YamlConfig config;
    public Secret secret;
    public EventSubWorker worker;
    public RewardRegistry registry;
    public RewardQueue queue;
    public ScheduledRunnable runnable;

    @Override
    public void onEnable() {
        main = this;

        // Load config
        this.config = new YamlConfig("config.yml");

        // Start runnable
        this.runnable = new ScheduledRunnable();
        this.runnable.runTaskTimer(this, 1, 1);

        // Load secret
        this.secret = new Secret();

        // Setup worker
        this.worker = new EventSubWorker(this.secret);
        this.worker.establishConnection();

        // Create registry
        this.registry = new RewardRegistry();

        // Init queue
        restartQueue();

        // Register commands
        new TwitchCommand();
    }

    @Override
    public void onDisable() {
        this.worker.stopServer();
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
