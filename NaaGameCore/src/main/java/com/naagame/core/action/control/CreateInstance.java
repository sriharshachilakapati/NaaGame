package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.resources.NgmEntity;

public class CreateInstance {
    private NgmEntity entity;

    private float posX;
    private float posY;

    private boolean relative;

    public NgmEntity getEntity() {
        return entity;
    }

    public void setEntity(NgmEntity entity) {
        this.entity = entity;
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

    static final class Definition extends ActionDefinition<CreateInstance> {

        public Definition() {
            super("control_create_instance", "Creates an instance of an entity at a position", CreateInstance::new);

            addArgument(ArgumentType.ENTITY,
                    CreateInstance::getEntity,
                    (o, v) -> o.setEntity((NgmEntity) v));

            addArgument(ArgumentType.FLOAT,
                    CreateInstance::getPosX,
                    (o, v) -> o.setPosX((Float) v));

            addArgument(ArgumentType.FLOAT,
                    CreateInstance::getPosY,
                    (o, v) -> o.setPosY((Float) v));

            addArgument(ArgumentType.BOOLEAN,
                    CreateInstance::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
