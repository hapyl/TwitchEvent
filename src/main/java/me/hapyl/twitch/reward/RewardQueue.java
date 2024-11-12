package me.hapyl.twitch.reward;

import com.google.common.collect.Lists;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.action.ParameterList;
import me.hapyl.twitch.util.Message;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;

public class RewardQueue extends BukkitRunnable {

    private final Queue<RewardRedemption> rewards;

    public RewardQueue() {
        this.rewards = Lists.newLinkedList();
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

        final ParameterList parameterList = reward.getParameterList();
        final boolean success = reward.getAction().perform(user, parameterList != null ? parameterList : ParameterList.EMPTY);

        if (success) {
            Message.success(reward.getMessage().replace("{user}", user.toString()));
        }
        else {
            final String messageFailed = reward.getMessageFailed();

            Message.error("Награда '{%s}' от {%s} отклонена! {%s}".formatted(
                    reward.getName(),
                    user.toString(),
                    messageFailed.replace("{user}", user.toString())
            ));
        }
    }

    public void add(RewardRedemption redemption) {
        this.rewards.add(redemption);
    }
}
