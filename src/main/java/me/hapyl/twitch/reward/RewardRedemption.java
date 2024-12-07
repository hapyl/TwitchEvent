package me.hapyl.twitch.reward;

import me.hapyl.twitch.TwitchUser;
import org.jspecify.annotations.NonNull;

public record RewardRedemption(@NonNull Reward reward, @NonNull TwitchUser user) {
}
