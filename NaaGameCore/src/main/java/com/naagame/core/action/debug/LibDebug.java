package com.naagame.core.action.debug;

import com.naagame.core.action.ActionDefinition;

public class LibDebug {
    public static final ActionDefinition<Log> LOG = new Log.Definition();
    public static final ActionDefinition<LogProps> LOG_PROPS = new LogProps.Definition();
}
