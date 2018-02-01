package com.naagame.core.action;

import java.util.function.Function;

public class ActionArgument<T> {

    public static final ActionArgument<String> STRING = new ActionArgument<>(
            x -> x.replaceAll(";", "\\;"),
            x -> x.replaceAll("\\\\;", ";")
    );

    public static final ActionArgument<ActionTarget> TARGET = new ActionArgument<>(
            x -> x.toString().toLowerCase(),
            x -> ActionTarget.valueOf(x.toUpperCase())
    );

    public static final ActionArgument<Float> FLOAT = new ActionArgument<>(String::valueOf, Float::parseFloat);
    public static final ActionArgument<Integer> INTEGER = new ActionArgument<>(String::valueOf, Integer::parseInt);
    public static final ActionArgument<Boolean> BOOLEAN = new ActionArgument<>(String::valueOf, Boolean::parseBoolean);

    private Function<T, String> encoder;
    private Function<String, T> decoder;

    private ActionArgument(Function<T, String> encoder, Function<String, T> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public Function<String, T> getDecoder() {
        return decoder;
    }

    public Function<T, String> getEncoder() {
        return encoder;
    }
}
