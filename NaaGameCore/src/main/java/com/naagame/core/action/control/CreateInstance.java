package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;

public class CreateInstance {
    private String entity;

    private float posX;
    private float posY;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
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

    static final class Definition extends ActionDefinition<CreateInstance> {

        public Definition() {
            super("control_create_instance", "Creates an instance of an entity at a position");

            addArgument(ArgumentType.STRING,
                    CreateInstance::getEntity,
                    (o, v) -> o.setEntity((String) v));

            addArgument(ArgumentType.FLOAT,
                    CreateInstance::getPosX,
                    (o, v) -> o.setPosX((Float) v));

            addArgument(ArgumentType.FLOAT,
                    CreateInstance::getPosY,
                    (o, v) -> o.setPosY((Float) v));
        }
    }
}
