package com.naagame.core.action;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ActionArgument<T> {
    private ArgumentType<Object> type;
    private Function<T, Object> getter;
    private BiConsumer<T, Object> setter;

    private String name;

    @SuppressWarnings("unchecked")
    ActionArgument(ArgumentType<?> type, String name, Function<T, Object> getter, BiConsumer<T, Object> setter) {
        this.type = (ArgumentType<Object>) type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public ArgumentType<Object> getType() {
        return type;
    }

    public Function<T, Object> getGetter() {
        return getter;
    }

    public BiConsumer<T, Object> getSetter() {
        return setter;
    }

    public String getName() {
        return name;
    }
}
