package me.hapyl.twitch.connection;

import me.hapyl.twitch.Secret;
import me.hapyl.twitch.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitchAuth {
    private final EventSubWorker worker;
    private final Secret secret;
    private final String authCode;

    private String token;

    public TwitchAuth(EventSubWorker worker, Secret secret, String authorizationCode) {
        this.worker = worker;
        this.secret = secret;
        this.authCode = authorizationCode;
    }

    public String getToken() throws Exception {
        if (token == null) {
            String tokenUrl = "https://id.twitch.tv/oauth2/token";
            String params = String.format(
                    "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                    secret.getClientId(), secret.getClientSecret(), authCode, Constants.LOCAL_HOST
            );

            URL url = new URL(tokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Extract the token from the JSON response
            final String json = response.toString();
            this.token = json.substring(json.indexOf("access_token\":\"") + 15, json.indexOf("\",\"expires_in"));

            this.worker.establishListener();
        }

        return this.token;
    }

}
