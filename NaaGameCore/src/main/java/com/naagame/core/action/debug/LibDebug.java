package com.naagame.core.action.debug;

import com.naagame.core.action.ActionDefinition;

public class LibDebug {
    public static final ActionDefinition<DebugLog> ACTION_LOG = new DebugLog.Definition();
    public static final ActionDefinition<DebugLogProps> ACTION_LOG_PROPS = new DebugLogProps.Definition();
}
