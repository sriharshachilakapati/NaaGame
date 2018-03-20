package com.naagame.core.action.movement;

import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;

public class MovementSetPosition {
    private ActionTarget target = ActionTarget.SELF;

    private float posX;
    private float posY;

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

    static final class Definition extends ActionDefinition<MovementSetPosition> {
        Definition() {
            super("movement_set_position", "Moves the entity to a specific position", MovementSetPosition::new);

            addArgument(ArgumentType.TARGET, "Target",
                    MovementSetPosition::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.FLOAT, "Position X",
                    MovementSetPosition::getPosX,
                    (o, v) -> o.setPosX((Float) v));

            addArgument(ArgumentType.FLOAT, "Position Y",
                    MovementSetPosition::getPosY,
                    (o, v) -> o.setPosY((Float) v));
        }
    }
}
