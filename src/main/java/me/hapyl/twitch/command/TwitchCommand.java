package me.hapyl.twitch.command;

import me.hapyl.twitch.Main;
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
    }
}
