package me.hapyl.twitch.connection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hapyl.twitch.Main;
import me.hapyl.twitch.TwitchUser;
import me.hapyl.twitch.reward.Reward;
import me.hapyl.twitch.reward.RewardRedemption;
import me.hapyl.twitch.util.Message;
import org.bukkit.util.NumberConversions;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

public class TwitchChannelPointsListener extends WebSocketClient {

    private static final URI THE_URL = java.net.URI.create("wss://pubsub-edge.twitch.tv");
    private static final Logger LOGGER = Main.getPlugin().getSLF4JLogger();
    private static final Gson GSON = new Gson();

    private final String accessToken;
    private final String channelId;

    public TwitchChannelPointsListener(String accessToken, String channelId) {
        super(THE_URL);
        this.accessToken = accessToken;
        this.channelId = channelId;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LOGGER.info("Establish connection to PubSub!");
        Message.success("Успешное подключение к Твичу!");

        // Subscribe to channel points topic
        final String message = ("{\"type\": \"LISTEN\", \"nonce\": \"random_nonce\",\"data\": {\"topics\": [\"channel-points-channel-v1.%s\"],\"auth_token\": \"%s\"}}").formatted(
                channelId,
                accessToken
        );

        send(message);

        // Schedule PING every 4 minutes to keep the connection alive
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LOGGER.info("[PING] Pinging twitch...");
                send("{\"type\": \"PING\"}");
            }
        }, 0, 240000);  // 240,000 ms = 4 minutes
    }

    @Override
    public void onMessage(String message) {
        final Main main = Main.getPlugin();

        final JsonObject json = GSON.fromJson(message, JsonObject.class);
        final JsonElement type = json.get("type");

        if (type == null) {
            return;
        }

        // Handle pong
        if (type.getAsString().equalsIgnoreCase("PONG")) {
            LOGGER.info("[PONG] Twitch responded!");
            return;
        }

        // I'm very lazy doing this the correct way fuck this
        try {
            final JsonObject baseJsonData = json.get("data").getAsJsonObject();
            final JsonObject baseJsonMessage = GSON.fromJson(baseJsonData.get("message").getAsString(), JsonObject.class);

            final JsonObject jsonData = baseJsonMessage.get("data").getAsJsonObject();
            final JsonObject jsonRedemption = jsonData.get("redemption").getAsJsonObject();

            // Get reward
            final JsonObject jsonReward = jsonRedemption.get("reward").getAsJsonObject();
            final String rewardTitle = jsonReward.get("title").getAsString();

            final Reward reward = main.registry.byTitle(rewardTitle);

            if (reward == null) {
                return;
            }

            // Get user
            final JsonObject user = jsonRedemption.get("user").getAsJsonObject();

            final int id = NumberConversions.toInt(user.get("id").getAsString());
            final String login = user.get("login").getAsString();
            final String displayName = user.get("display_name").getAsString();

            final TwitchUser twitchUser = new TwitchUser(id, login, displayName);

            main.queue.add(new RewardRedemption(reward, twitchUser));
        } catch (NullPointerException ignored) { // Don't care, invalid response

        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.info("Disconnected from PubSub! (%s: %s)".formatted(code, reason));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
