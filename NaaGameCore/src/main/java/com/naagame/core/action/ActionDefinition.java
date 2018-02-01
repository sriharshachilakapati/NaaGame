package com.naagame.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class ActionDefinition<T> {
    private String code;
    private String desc;

    private List<ActionArgument<Object>> arguments;

    private Map<ActionArgument<Object>, Function<T, Object>> argGetterMap;
    private Map<ActionArgument<Object>, BiConsumer<T, Object>> argSetterMap;

    public ActionDefinition(String code, String desc) {
        this.code = code;
        this.desc = desc;

        this.arguments = new ArrayList<>();

        this.argGetterMap = new HashMap<>();
        this.argSetterMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    protected void addArgument(ActionArgument<?> argument, Function<T, Object> getter, BiConsumer<T, Object> setter) {
        arguments.add((ActionArgument<Object>) argument);
        argGetterMap.put((ActionArgument<Object>) argument, getter);
        argSetterMap.put((ActionArgument<Object>) argument, setter);
    }

    public String encode(T object) {
        StringBuilder builder = new StringBuilder();

        for (ActionArgument<Object> argument : arguments) {
            Object value = argGetterMap.get(argument).apply(object);
            String encoded = argument.getEncoder().apply(value);
            builder.append(encoded).append(";");
        }

        return builder.toString();
    }

    public T decode(String args, T object) {
        String[] parts = args.split(";");
        int argIndex = 0;

        for (ActionArgument<Object> argument : arguments) {
            BiConsumer<T, Object> setter = argSetterMap.get(argument);
            Object decoded = argument.getDecoder().apply(parts[argIndex]);
            setter.accept(object, decoded);
            argIndex++;
        }

        return object;
    }

    public List<ActionArgument<Object>> getArguments() {
        return arguments;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return desc;
    }
}
