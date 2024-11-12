package me.hapyl.twitch.util;

import me.hapyl.twitch.network.Ip;

public final class Constants {

    public static final Ip LOCAL_HOST = Ip.localhost(8080, "callback");

    private Constants() {
    }


}
