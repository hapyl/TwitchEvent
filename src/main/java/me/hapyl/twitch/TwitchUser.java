package me.hapyl.twitch;

import org.jetbrains.annotations.NotNull;

public class TwitchUser {

    public static final TwitchUser DEBUG = new TwitchUser(0, "debug", "debug");

    private final int id;
    private final String login;
    private final String displayName;

    public TwitchUser(int id, String login, String displayName) {
        this.id = id;
        this.login = login;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
