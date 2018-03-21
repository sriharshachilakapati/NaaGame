package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class SetSpeed {
    private ActionTarget target = ActionTarget.SELF;

    private float hSpeed;
    private float vSpeed;

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

    static final class Definition extends ActionDefinition<SetSpeed> {
        Definition() {
            super("movement_set_speed", "Sets the speed of the entity", SetSpeed::new);

            addArgument(ArgumentType.TARGET, "Target",
                    SetSpeed::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT, "HSpeed",
                    SetSpeed::getHSpeed,
                    (o, v) -> o.setHSpeed((Float) v));

            addArgument(ArgumentType.FLOAT, "VSpeed",
                    SetSpeed::getVSpeed,
                    (o, v) -> o.setVSpeed((Float) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    SetSpeed::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
