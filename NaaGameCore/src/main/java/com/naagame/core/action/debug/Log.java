package com.naagame.core.action.debug;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;

public class Log {
    private String message = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    static final class Definition extends ActionDefinition<Log> {
        Definition() {
            super("debug_log", "Logs a message to the debug console", Log::new);
            addArgument(ArgumentType.STRING, "Message",
                    Log::getMessage,
                    (o, s) -> o.setMessage(String.valueOf(s)));
        }
    }
}
