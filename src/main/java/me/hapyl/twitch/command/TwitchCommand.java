package me.hapyl.twitch.command;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.Reward;
import me.hapyl.twitch.util.Message;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class TwitchCommand extends ICommand {
    public TwitchCommand() {
        super("twitch");
        super.register();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull @NonNull String[] args) {
        // twitch reload
        // twitch redeem (name...)

        if (args.length == 1) {
            final String arg = args[0];
            switch (arg.toLowerCase()) {
                case "reload" -> {
                    final Main plugin = Main.getPlugin();

                    plugin.config.load();

                    // Reload rewards
                    plugin.registry.reload();

                    Message.success("Конфиг успешно перезагружен!");
                }
                // TODO (Tue, Nov 12 2024 @xanyjl): impl reloading
            }
        }
        else if (args.length >= 2) {
            final String argument0 = args[0];

            if (!argument0.equalsIgnoreCase("redeem")) {
                return;
            }

            final String title = buildString(args, 1);
            final Reward reward = Main.getPlugin().registry.byTitle(title);

            if (reward == null) {
                Message.error("Неизвестная награда: {%s}!".formatted(title));
                return;
            }

            final boolean success = reward.getAction().perform(TwitchUser.DEBUG, reward.getParameterList());

            if (success) {
                Message.success("Награда да");
            }
            else {
                Message.error("Награда нет");
            }
        }
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
