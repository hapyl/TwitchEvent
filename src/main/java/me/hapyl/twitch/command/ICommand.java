package me.hapyl.twitch.command;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.util.Strict;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public abstract class ICommand implements CommandExecutor {

    private final String name;

    protected ICommand(String name) {
        this.name = name;
    }

    public abstract void onCommand(@NotNull CommandSender sender, @NonNull String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        onCommand(sender, args);
        return true;
    }

    protected void register() {
        final PluginCommand command = Main.getPlugin().getCommand(name);

        if (command != null) {
            command.setExecutor(this);
        }
    }
}
