package jsonObjects;

import org.jetbrains.annotations.NotNull;

public class Error implements JsonObjects {
    private final String type;
    private final String message;

    public Error(@NotNull final  String type, @NotNull final String message) {
        this.type = type;
        this.message = message;
    }
}
