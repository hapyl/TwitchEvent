package me.hapyl.twitch.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IpImpl implements Ip {

    private final String hostName;
    private final int port;
    private final String context;
    private String toString;

    IpImpl(String hostName, int port, String context) {
        this.hostName = hostName;
        this.port = port;
        this.context = context;
    }

    @Override
    public final String toString() {
        if (toString == null) {
            toString = hostName + ":" + port;

            if (context != null) {
                toString += "/" + context;
            }
        }

        return toString;
    }

    @Override
    @NotNull
    public String getHostName() {
        return hostName;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public @Nullable String getContext() {
        return context;
    }

}
