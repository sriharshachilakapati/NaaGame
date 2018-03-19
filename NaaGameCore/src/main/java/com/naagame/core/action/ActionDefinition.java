package com.naagame.core.action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ActionDefinition<T> {
    private String code;
    private String desc;
    private Supplier<T> supplier;

    private List<ActionArgument<T>> arguments;

    public ActionDefinition(String code, String desc, Supplier<T> supplier) {
        this.code = code;
        this.desc = desc;
        this.supplier = supplier;

        this.arguments = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    protected void addArgument(ArgumentType<?> argument, Function<T, Object> getter, BiConsumer<T, Object> setter) {
        arguments.add(new ActionArgument<>(argument, getter, setter));
    }

    public String encode(T object) {
        StringBuilder builder = new StringBuilder();

        for (ActionArgument<T> argument : arguments) {
            Object value = argument.getGetter().apply(object);
            String encoded = argument.getType().getEncoder().apply(value);
            builder.append(encoded).append(";");
        }

        return builder.toString();
    }

    public T decode(String args, T object) {
        String[] parts = args.split(";");
        int argIndex = 0;

        for (ActionArgument<T> argument : arguments) {
            BiConsumer<T, Object> setter = argument.getSetter();
            Object decoded = argument.getType().getDecoder().apply(parts[argIndex]);
            setter.accept(object, decoded);
            argIndex++;
        }

        return object;
    }

    public List<ActionArgument<T>> getArguments() {
        return arguments;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return desc;
    }

    public Supplier<T> getSupplier() {
        return supplier;
    }
}
