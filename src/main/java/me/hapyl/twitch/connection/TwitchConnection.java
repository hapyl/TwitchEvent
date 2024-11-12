package me.hapyl.twitch.connection;

import com.sun.net.httpserver.HttpServer;
import me.hapyl.twitch.Secret;
import me.hapyl.twitch.util.Constants;
import me.hapyl.twitch.util.Strict;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class TwitchConnection {

    public final HttpServer server;
    private String code;

    public TwitchConnection(EventSubWorker worker, Secret secret) throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(Constants.LOCAL_HOST.getPort()), 0);

        this.server.createContext("/" + Constants.LOCAL_HOST.getContext(), exchange -> {
            String query = exchange.getRequestURI().getQuery();

            if (query != null && query.contains("code=")) {
                code = query.substring(query.indexOf("code=") + 5, query.indexOf("&scope"));

                worker.establishAuthentication();
            }

            String response = "Success! You can close this window.";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        server.start();
        openTwitchAuthPage(secret.getClientId(), Constants.LOCAL_HOST.toString());
    }

    public @NotNull String getCode() {
        return code;
    }

    public static void openTwitchAuthPage(String clientId, String redirectUri) {
        try {
            URI uri = new URI(
                    "https://id.twitch.tv/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=channel:read:redemptions".formatted(
                            clientId,
                            redirectUri
                    ));

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            }
            else {
                Strict.unsupportedOp("open browser");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
