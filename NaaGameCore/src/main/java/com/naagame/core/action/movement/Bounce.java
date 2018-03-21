package com.naagame.core.action.movement;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;
import com.naagame.core.action.ArgumentType;

public class Bounce {
    private ActionTarget target;

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

    static class Definition extends ActionDefinition<Bounce> {
        Definition() {
            super("movement_bounce", "Bounces an entity off of another entity", Bounce::new);

            addArgument(ArgumentType.TARGET, "Target",
                    Bounce::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));
        }
    }
}
