package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class SetPosition {
    private ActionTarget target = ActionTarget.SELF;

    private float posX;
    private float posY;

    private boolean relative;

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    static final class Definition extends ActionDefinition<SetPosition> {
        Definition() {
            super("movement_set_position", "Moves the entity to a specific position", SetPosition::new);

            addArgument(ArgumentType.TARGET, "Target",
                    SetPosition::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT, "Position X",
                    SetPosition::getPosX,
                    (o, v) -> o.setPosX((Float) v));

            addArgument(ArgumentType.FLOAT, "Position Y",
                    SetPosition::getPosY,
                    (o, v) -> o.setPosY((Float) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    SetPosition::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
