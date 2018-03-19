package com.naagame.core.action;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;

import java.util.function.Function;

public class ArgumentType<T> {

    public static final ArgumentType<String> STRING = new ArgumentType<>(
            x -> x.replaceAll(";", "\\;"),
            x -> x.replaceAll("\\\\;", ";")
    );

    public static final ArgumentType<NgmEntity> ENTITY = new ArgumentType<>(
            x -> x == null ? "" : x.getName(),
            x -> NgmProject.find(NgmProject.entities, x)
    );

    public static final ArgumentType<ActionTarget> TARGET = new ArgumentType<>(
            x -> x.toString().toLowerCase(),
            x -> ActionTarget.valueOf(x.toUpperCase())
    );

    public static final ArgumentType<Float> FLOAT = new ArgumentType<>(String::valueOf, Float::parseFloat);
    public static final ArgumentType<Integer> INTEGER = new ArgumentType<>(String::valueOf, Integer::parseInt);
    public static final ArgumentType<Boolean> BOOLEAN = new ArgumentType<>(String::valueOf, Boolean::parseBoolean);

    private Function<T, String> encoder;
    private Function<String, T> decoder;

    private ArgumentType(Function<T, String> encoder, Function<String, T> decoder) {
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
