package me.hapyl.twitch.command;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.Reward;
import me.hapyl.twitch.util.Message;
import me.hapyl.twitch.util.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class TwitchCommand extends ICommand {
    public TwitchCommand() {
        super("twitch");

        super.register();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull @NonNull String[] args) {
        if (args.length >= 1) {
            final String argument0 = args[0];

            if (argument0.equalsIgnoreCase("reload")) {
                final Main plugin = Main.getPlugin();

                plugin.config.load();
                plugin.registry.reload();

                Message.success("Конфиг успешно перезагружен!");
            }
            else if (args.length >= 2 && argument0.equalsIgnoreCase("redeem")) {
                final String title = buildString(args, 1);
                final Reward reward = Main.getPlugin().registry.byTitle(title);

                if (reward == null) {
                    Message.error("Неизвестная награда: {%s}!".formatted(title));
                    return;
                }

                final boolean success = reward.getAction().perform(TwitchUser.DEBUG, reward.getParameterList());

                Message.info("Награда {%s} вызвана: %s".formatted(
                        title,
                        success
                                ? "&2✔ Успех!"
                                : "&4❌ Провал!"
                ));
            }
            else {
                Message.error("Неверный формат команды! {%s}".formatted("/twitch (reload, redeem) [name...]"));
            }
        }

    }

    @Override
    @NonNull
    public List<String> onTabComplete(@NonNull CommandSender sender, @NotNull @NonNull String[] args) {
        if (args.length == 1) {
            return super.sorted(List.of("reload", "redeem"), args);
        }
        else if (args[0].equalsIgnoreCase("redeem")) {
            return super.sorted(Main.getPlugin().registry.getRewards().keySet(), args);
        }

        return List.of();
    }

    private void sendInvalidUsage(String correctUsage) {
        Message.error("Неверный формат команды! {%s}".formatted(correctUsage));
    }

    private String buildString(String[] args, int startIndex) {
        final StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < args.length; i++) {
            if (i != 0) {
                builder.append(" ");
            }

            builder.append(args[i]);
        }

        return builder.toString().trim();
    }
}
