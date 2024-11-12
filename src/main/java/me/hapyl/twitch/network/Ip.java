package me.hapyl.twitch.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public interface Ip {

    @NonNull
    String getHostName();

    int getPort();

    @Nullable
    String getContext();

    static Ip of(@NonNull String hostName, int port, @Nullable String context) {
        return new IpImpl(hostName, port, context);
    }

    static Ip localhost(int port, @Nullable String context) {
        return of("http://localhost", port, context);
    }

}
