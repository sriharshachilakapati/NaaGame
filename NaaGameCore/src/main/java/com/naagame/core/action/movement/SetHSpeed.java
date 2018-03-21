package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class SetHSpeed {
    private ActionTarget target = ActionTarget.SELF;

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

    static final class Definition extends ActionDefinition<SetHSpeed> {
        Definition() {
            super("movement_set_hspeed", "Sets the horizontal speed of the entity", SetHSpeed::new);

            addArgument(ArgumentType.TARGET, "Target",
                    SetHSpeed::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT, "HSpeed",
                    SetHSpeed::getHSpeed,
                    (o, v) -> o.setHSpeed((Float) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    SetHSpeed::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
