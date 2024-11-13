package me.hapyl.twitch.connection;

import me.hapyl.twitch.Main;
import me.hapyl.twitch.Secret;
import org.slf4j.Logger;

public class EventSubWorker {

    private static final Logger LOGGER;

    static {
        final Main plugin = Main.getPlugin();

        LOGGER = plugin.getSLF4JLogger();
    }

    private final Secret secret;

    private TwitchConnection connection;
    private TwitchAuth auth;
    private TwitchChannelPointsListener listener;

    public EventSubWorker(Secret secret) {
        this.secret = secret;
    }

    public void establishConnection() {
        try {
            LOGGER.info("Opening a connection to retrieve the code...");

            this.connection = new TwitchConnection(this, secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void establishAuthentication() {
        try {
            this.auth = new TwitchAuth(this, secret, connection.getCode());
            this.auth.getToken(); // call to get first
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void establishListener() {
        try {
            this.listener = new TwitchChannelPointsListener(auth.getToken(), secret.getChannelId());
            this.listener.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        this.connection.server.stop(0);
        this.listener.close();
    }
}
