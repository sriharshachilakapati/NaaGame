package com.naagame.core.action.debug;

import com.naagame.core.action.ActionDefinition;

public class LibDebug {
    public static final ActionDefinition<DebugLog> LOG = new DebugLog.Definition();
    public static final ActionDefinition<DebugLogProps> LOG_PROPS = new DebugLogProps.Definition();
}
