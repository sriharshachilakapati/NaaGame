package com.naagame.core.action.movement;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.resources.NgmEntity;

public class SetPositionRelativeTo {
    private ActionTarget target = ActionTarget.SELF;

    private NgmEntity entity;

    private float x;
    private float y;

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }

    public NgmEntity getEntity() {
        return entity;
    }

    public void setEntity(NgmEntity entity) {
        this.entity = entity;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    static class Definition extends ActionDefinition<SetPositionRelativeTo> {
        Definition() {
            super("movement_set_position_relative_to", "", SetPositionRelativeTo::new);

            addArgument(ArgumentType.TARGET, "Target",
                    SetPositionRelativeTo::getTarget,
                    (o, v) -> o.setTarget((ActionTarget) v));

            addArgument(ArgumentType.ENTITY, "Entity",
                    SetPositionRelativeTo::getEntity,
                    (o, v) -> o.setEntity((NgmEntity) v));

            addArgument(ArgumentType.FLOAT, "X",
                    SetPositionRelativeTo::getX,
                    (o, v) -> o.setX((Float) v));

            addArgument(ArgumentType.FLOAT, "Y",
                    SetPositionRelativeTo::getY,
                    (o, v) -> o.setY((Float) v));
        }
    }
}
