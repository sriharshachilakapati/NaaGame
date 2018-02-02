package com.naagame.core.action.debug;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class DebugLogProps {
    private ActionTarget target;

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

    static final class Definition extends ActionDefinition<DebugLogProps> {
        Definition() {
            super("debug_log_props", "Logs the properties of an entity instance");

            addArgument(ArgumentType.TARGET,
                    DebugLogProps::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));
        }
    }
}
