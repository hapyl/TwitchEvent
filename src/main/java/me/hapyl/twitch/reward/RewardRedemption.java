package me.hapyl.twitch.reward;

import me.hapyl.twitch.TwitchUser;

public record RewardRedemption(Reward reward, TwitchUser user) {
}
