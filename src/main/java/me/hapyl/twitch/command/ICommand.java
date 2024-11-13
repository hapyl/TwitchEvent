package me.hapyl.twitch.command;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.util.Strict;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ICommand implements CommandExecutor, TabCompleter {

    private final String name;

    protected ICommand(String name) {
        this.name = name;
    }

    public abstract void onCommand(@NotNull CommandSender sender, @NonNull String[] args);

    @NonNull
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull String[] args) {
        return List.of();
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        onCommand(sender, args);
        return true;
    }

    @Override
    @NotNull
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return onTabComplete(sender, args);
    }

    protected void register() {
        final PluginCommand command = Main.getPlugin().getCommand(name);

        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @NonNull
    protected List<String> sorted(@NonNull Collection<String> strings, @NotNull String[] args) {
        final List<String> result = new ArrayList<>();
        final String latest = args[args.length - 1];

        for (Object obj : strings) {
            final String str = String.valueOf(obj);

            if (str.startsWith(latest)) {
                result.add(str);
            }
        }

        return result;
    }

}
