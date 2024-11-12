package me.hapyl.twitch;

import me.hapyl.twitch.util.Strict;
import org.jetbrains.annotations.NotNull;

public class Secret extends YamlConfig {

    private final String clientId;
    private final String clientSecret;
    private final String channelId; // this is not really a secret, but I don't care

    public Secret() {
        super("secret.yml");

        this.clientId = yaml.getString("client_id");
        this.clientSecret = yaml.getString("client_secret");
        this.channelId = yaml.getString("channel_id");

        Strict.notNull(this.clientId, "Missing 'client_id' in secret.yml!");
        Strict.notNull(this.clientSecret, "Missing 'client_secret' in secret.yml!");
        Strict.notNull(this.channelId, "Missing 'channel_id' in secret.yml!");
    }

    @NotNull
    public String getClientId() {
        return clientId;
    }

    @NotNull
    public String getClientSecret() {
        return clientSecret;
    }

    @NotNull
    public String getChannelId() {
        return channelId;
    }
}
