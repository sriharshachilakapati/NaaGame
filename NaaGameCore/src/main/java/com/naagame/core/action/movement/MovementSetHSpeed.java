package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class MovementSetHSpeed {
    private ActionTarget target;

    private float hSpeed;

    private boolean relative;

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

    public float getHSpeed() {
        return hSpeed;
    }

    public void setHSpeed(float hSpeed) {
        this.hSpeed = hSpeed;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    static final class Definition extends ActionDefinition<MovementSetHSpeed> {
        Definition() {
            super("movement_set_hspeed", "Sets the horizontal speed of the entity");

            addArgument(ArgumentType.TARGET,
                    MovementSetHSpeed::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT,
                    MovementSetHSpeed::getHSpeed,
                    (o, v) -> o.setHSpeed((Float) v));

            addArgument(ArgumentType.BOOLEAN,
                    MovementSetHSpeed::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
