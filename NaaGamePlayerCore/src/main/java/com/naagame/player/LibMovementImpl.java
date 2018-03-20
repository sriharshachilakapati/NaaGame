package com.naagame.player;

import com.naagame.core.action.movement.*;
import com.naagame.core.resources.NgmEntity;
import com.shc.silenceengine.math.Vector3;

class LibMovementImpl {
    private static MovementSetSpeed movementSetSpeed = new MovementSetSpeed();
    private static MovementSetHSpeed movementSetHSpeed = new MovementSetHSpeed();
    private static MovementSetVSpeed movementSetVSpeed = new MovementSetVSpeed();
    private static MovementSetPosition movementSetPosition = new MovementSetPosition();

    static void setSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_SPEED.decode(action.getArgs(), movementSetSpeed);

        EntityInstance instance = null;

        switch (movementSetSpeed.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        float hSpeed = movementSetSpeed.getHSpeed();
        float vSpeed = movementSetSpeed.getVSpeed();

        if (movementSetSpeed.isRelative()) {
            hSpeed += instance.speed.x;
            vSpeed += instance.speed.y;
        }

        instance.speed.set(hSpeed, vSpeed);
    }

    static void setHSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_HSPEED.decode(action.getArgs(), movementSetHSpeed);

        EntityInstance instance = null;

        switch (movementSetHSpeed.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        if (movementSetHSpeed.isRelative()) {
            instance.speed.x += movementSetHSpeed.getHSpeed();
        } else {
            instance.speed.x = movementSetHSpeed.getHSpeed();
        }
    }

    static void setVSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_VSPEED.decode(action.getArgs(), movementSetVSpeed);

        EntityInstance instance = null;

        switch (movementSetVSpeed.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        if (movementSetVSpeed.isRelative()) {
            instance.speed.y += movementSetVSpeed.getVSpeed();
        } else {
            instance.speed.y = movementSetVSpeed.getVSpeed();
        }
    }

    static void setPosition(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_POSITION.decode(action.getArgs(), movementSetPosition);

        EntityInstance instance = null;

        switch (movementSetPosition.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        float posX = movementSetPosition.getPosX();
        float posY = movementSetPosition.getPosY();

        Vector3 selfPos = self.transformComponent.getPosition();

        if (movementSetPosition.isRelative()) {
            posX += selfPos.x;
            posY += selfPos.y;
        }

        instance.transformComponent.setPosition(posX, posY);
    }
}
