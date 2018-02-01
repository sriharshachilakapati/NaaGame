package com.naagame.core.action.movement;

import com.naagame.core.action.ActionArgument;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class MovementSetVSpeed {
    private ActionTarget target;

    private float vSpeed;

    private boolean relative;

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

    public float getVSpeed() {
        return vSpeed;
    }

    public void setVSpeed(float vSpeed) {
        this.vSpeed = vSpeed;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    static final class Definition extends ActionDefinition<MovementSetVSpeed> {
        Definition() {
            super("movement_set_vspeed", "Sets the vertical speed of the entity");

            addArgument(ActionArgument.TARGET,
                    MovementSetVSpeed::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ActionArgument.FLOAT,
                    MovementSetVSpeed::getVSpeed,
                    (o, v) -> o.setVSpeed((Float) v));

            addArgument(ActionArgument.BOOLEAN,
                    MovementSetVSpeed::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
