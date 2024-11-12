package me.hapyl.twitch.reward;

import com.google.common.collect.Maps;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.YamlConfig;
import me.hapyl.twitch.reward.action.ExpectedParameterKeySet;
import me.hapyl.twitch.reward.action.ParameterList;
import me.hapyl.twitch.reward.action.TAction;
import me.hapyl.twitch.reward.action.TActions;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.Strict;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Set;

public class RewardRegistry {

    private static final Logger LOGGER = Main.getPlugin().getSLF4JLogger();

    // This stores the reward name, not id
    private final Map<String, Reward> rewards;

    public RewardRegistry() {
        this.rewards = Maps.newHashMap();

        // Load rewards
        this.reload();
    }

    public void reload() {
        this.rewards.clear();

        // Load from config and handle exceptions
        final Main plugin = Main.getPlugin();
        final YamlConfig config = plugin.config;

        final ConfigurationSection rewards = config.getYaml().getConfigurationSection("rewards");

        if (rewards == null) {
            throw Strict.nullPointer("Missing 'rewards' field in config.yml!");
        }

        final RewardReader reader = new RewardReader(rewards);
        reader.readAll(this);
    }

    @Nullable
    public Reward byTitle(String rewardTitle) {
        return this.rewards.get(rewardTitle);
    }

    @NonNull
    public Map<String, Reward> getRewards() {
        return rewards;
    }

    private static class RewardReader {

        private final ConfigurationSection rewards;

        private RewardReader(ConfigurationSection rewards) {
            this.rewards = rewards;
        }

        public void readAll(RewardRegistry registry) {
            for (String key : rewards.getKeys(false)) {
                final KeyReader reader = new KeyReader(rewards, key);

                final String rewardName = reader.read("reward_name");
                final String message = reader.readOrDefault("message", "{user} redeemed %s!".formatted(rewardName));
                final String messageFailed = reader.readOrDefault("message_failed", "Не повезло!");

                final String actionName = reader.read("action.name");
                final TAction action = TActions.byName(actionName);

                if (action == null) {
                    errorLoadingReward(key, "Неизвестное название функции: %s!".formatted(actionName));
                    return;
                }

                // Don't load ANY rewards if failed
                if (reader.fail) {
                    return;
                }

                // Read parameters
                final ConfigurationSection parameters = rewards.getConfigurationSection("%s.action.parameters".formatted(key));
                final ExpectedParameterKeySet exceptedParameters = action.getExceptedParameters();

                ParameterList parameterList = null;

                // If param section is null and expected params also null we don't really care
                if (parameters == null) {
                    if (!exceptedParameters.isEmpty()) {
                        errorLoadingReward(key, "Для этой награды нужно поля 'parameters'!");
                        return;
                    }
                }
                // Else process parameters
                else {
                    final Map<String, String> parameterMap = Maps.newHashMap();

                    parameters.getKeys(false).forEach(parameter -> {
                        final String value = rewards.getString("%s.action.parameters.%s".formatted(key, parameter));

                        parameterMap.put(parameter, value);
                    });

                    // Check for missing parameters
                    final Set<String> expectedKeys = action.getExceptedParameters().getKeys().keySet();

                    for (String expectedKey : expectedKeys) {
                        if (!parameterMap.containsKey(expectedKey)) {
                            errorLoadingReward(rewardName, "Отсутствует параметр '%s'!".formatted(expectedKey));
                            return;
                        }
                    }

                    parameterList = new ParameterList(parameterMap);
                }

                // Load reward
                final Reward value = new Reward(rewardName, action, message, messageFailed, parameterList);
                registry.rewards.put(rewardName, value);

                LOGGER.info("Loaded reward '%s'!".formatted(rewardName));
            }

            Message.success("Все награды успешно загружены!");
        }

        private static void errorLoadingReward(String rewardName, String reason) {
            Message.error("Ошибка загрузки награды '{%s}'!".formatted(rewardName));
            Message.error("Причина: " + reason);
        }

        private static class KeyReader {

            private final ConfigurationSection section;
            private final String key;

            boolean fail;

            private KeyReader(ConfigurationSection section, String key) {
                this.section = section;
                this.key = key;
            }

            @NonNull
            public String readOrDefault(@NonNull String name, String def) {
                final String string = section.getString(key + "." + name);

                return string != null ? string : def;
            }

            @NonNull
            public String read(@NonNull String name) {
                final String string = section.getString(key + "." + name);

                if (string != null) {
                    return string;
                }

                errorLoadingReward(key, "Нет поля '%s'!".formatted(key + "." + name));

                fail = true;
                return ""; // Don't return null
            }
        }
    }


}
