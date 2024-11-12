package me.hapyl.twitch;

import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.InputStream;

public class YamlConfig {

    private final String name;
    private final File file;

    protected YamlConfiguration yaml;

    public YamlConfig(@NonNull String name) {
        Validate.isTrue(name.endsWith(".yml"), "The file name must end with '.yml'!");
        final Main plugin = Main.getPlugin();

        this.name = name;
        this.file = new File(plugin.getDataFolder(), this.name);

        // Copy default config if exists
        final InputStream resource = plugin.getResource(name);

        if (resource != null && !file.exists()) {
            plugin.saveResource(name, true);
        }

        load(); // force load
    }

    public void reload() {
        this.load(); // Do not care about saving, we're just reading files
    }

    public void load() {
        try {
            this.yaml = new YamlConfiguration();
            this.yaml.load(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public YamlConfiguration getYaml() {
        if (yaml == null) {
            load();
        }

        return yaml;
    }
}
