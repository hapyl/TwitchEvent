package me.hapyl.twitch.reward;

import com.google.common.collect.Lists;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.TAction;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class RewardQueue extends BukkitRunnable {

    private final Queue<RewardRedemption> rewards;

    public RewardQueue() {
        this.rewards = Lists.newLinkedList();
    }

    public void executeReward(@NonNull Player player, @NonNull Reward reward, @NonNull TwitchUser user) {
        final TAction action = reward.action();
        final ParameterList parameterList = reward.parameterList();
        final boolean isSuccess = action.perform(player, user, parameterList);

        if (isSuccess) {
            action.onSuccess(player, user, reward);
        }
        else {
            action.onFail(player, user, reward);
        }
    }

    @Override
    public void run() {
        final RewardRedemption next = rewards.poll();

        // Don't care
        if (next == null) {
            return;
        }

        final Reward reward = next.reward();
        final TwitchUser user = next.user();

        final List<Player> players = getPlayers();

        if (players == null) {
            Message.error("Невозможно выполнить награду, никого нет онлайн!");
            return;
        }

        for (Player player : players) {
            executeReward(player, reward, user);
        }
    }

    public void add(RewardRedemption redemption) {
        this.rewards.add(redemption);
    }

    @Nullable
    private static List<Player> getPlayers() {
        final List<Player> onlinePlayers = Lists.newArrayList(Bukkit.getOnlinePlayers());
        final boolean isShared = Main.getPlugin().config.getYaml().getBoolean("shared_punishment");

        if (onlinePlayers.isEmpty()) {
            return null;
        }

        if (isShared) {
            return onlinePlayers;
        }

        Collections.shuffle(onlinePlayers);
        return List.of(onlinePlayers.getFirst());
    }
}
