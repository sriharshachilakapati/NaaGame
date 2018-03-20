package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class MovementSetSpeed {
    private ActionTarget target;

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

    static final class Definition extends ActionDefinition<MovementSetSpeed> {
        Definition() {
            super("movement_set_speed", "Sets the speed of the entity", MovementSetSpeed::new);

            addArgument(ArgumentType.TARGET, "Target",
                    MovementSetSpeed::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT, "HSpeed",
                    MovementSetSpeed::getHSpeed,
                    (o, v) -> o.setHSpeed((Float) v));

            addArgument(ArgumentType.FLOAT, "VSpeed",
                    MovementSetSpeed::getVSpeed,
                    (o, v) -> o.setVSpeed((Float) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    MovementSetSpeed::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
