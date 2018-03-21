package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class SetVSpeed {
    private ActionTarget target = ActionTarget.SELF;

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

    static final class Definition extends ActionDefinition<SetVSpeed> {
        Definition() {
            super("movement_set_vspeed", "Sets the vertical speed of the entity", SetVSpeed::new);

            addArgument(ArgumentType.TARGET, "Target",
                    SetVSpeed::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT, "VSpeed",
                    SetVSpeed::getVSpeed,
                    (o, v) -> o.setVSpeed((Float) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    SetVSpeed::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
