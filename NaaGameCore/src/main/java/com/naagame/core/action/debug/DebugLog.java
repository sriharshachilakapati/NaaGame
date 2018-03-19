package com.naagame.core.action.debug;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;

public class DebugLog {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    static final class Definition extends ActionDefinition<DebugLog> {
        Definition() {
            super("debug_log", "Logs a message to the debug console", DebugLog::new);
            addArgument(ArgumentType.STRING, DebugLog::getMessage, (o, s) -> o.setMessage(String.valueOf(s)));
        }
    }
}
