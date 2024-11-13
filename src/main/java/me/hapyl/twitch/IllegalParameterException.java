package me.hapyl.twitch;

import org.jspecify.annotations.NonNull;

public class IllegalParameterException extends IllegalArgumentException {

    public IllegalParameterException(@NonNull String paramName, @NonNull String reason) {
        super("Illegal parameter '%s'! %s".formatted(paramName, reason));
    }

}
