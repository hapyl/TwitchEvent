package me.hapyl.twitch.reward;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.twitch.IllegalParameterException;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.YamlConfig;
import me.hapyl.twitch.reward.action.TCompletableAction;
import me.hapyl.twitch.reward.action.param.ParameterList;
import me.hapyl.twitch.reward.action.TAction;
import me.hapyl.twitch.reward.action.TActions;
import me.hapyl.twitch.util.Enums;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.Strict;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

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

                // Load item rewards if present
                final List<ItemStack> itemRewards = Lists.newArrayList();
                final ConfigurationSection itemRewardsSection = rewards.getConfigurationSection("%s.item_rewards".formatted(key));

                if (itemRewardsSection != null) {
                    // Check if the action can actually be completed
                    if (!(action instanceof TCompletableAction)) {
                        errorLoadingReward(key, "Награда {%s} не поддерживает награды!".formatted(key));
                        return;
                    }

                    for (String itemId : itemRewardsSection.getKeys(false)) {
                        final Material material = Enums.byName(Material.class, itemId);
                        final int amount = itemRewardsSection.getInt(itemId, 1);

                        if (material == null) {
                            errorLoadingReward(key, "Неизвестный предмет: {%s}!".formatted(itemId));
                            return;
                        }

                        if (!material.isItem()) {
                            errorLoadingReward(
                                    key,
                                    "Предмет должен быть предметом, а не блоком! ({%s})".formatted(material.getKey().getKey())
                            );
                            return;
                        }

                        if (amount < 1 || amount > 99) {
                            errorLoadingReward(key, "Количество предмета не может быть меньше 1 или больше 99!");
                            return;
                        }

                        itemRewards.add(new ItemStack(material, amount));
                    }
                }

                // Don't load ANY rewards if failed
                if (reader.fail) {
                    return;
                }

                // Read parameters
                final ConfigurationSection parameters = rewards.getConfigurationSection("%s.action.parameters".formatted(key));

                ParameterList parameterList = ParameterList.EMPTY;

                // If param section is null and expected params also null we don't really care
                // Else process parameters
                if (parameters != null) {
                    final Map<String, String> parameterMap = Maps.newHashMap();

                    parameters.getKeys(false).forEach(parameter -> {
                        final String value = rewards.getString("%s.action.parameters.%s".formatted(key, parameter));

                        parameterMap.put(parameter, value);
                    });

                    parameterList = new ParameterList(parameterMap);
                }

                // Validate parameters
                try {
                    action.validateParameters(parameterList);
                } catch (IllegalParameterException ex) {
                    errorLoadingReward(key, ex.getMessage());
                    return;
                }

                // Load reward
                registry.rewards.put(rewardName, new Reward(rewardName, action, message, messageFailed, parameterList, itemRewards));

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

            public boolean contains(@NonNull String name) {
                return section.contains(name);
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
