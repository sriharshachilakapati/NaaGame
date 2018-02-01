package com.naagame.player;

import com.naagame.core.action.movement.LibMovement;
import com.naagame.core.action.movement.MovementSetHSpeed;
import com.naagame.core.action.movement.MovementSetSpeed;
import com.naagame.core.action.movement.MovementSetVSpeed;
import com.naagame.core.resources.NgmEntity;

class LibMovementImpl {
    private static MovementSetSpeed movementSetSpeed = new MovementSetSpeed();
    private static MovementSetHSpeed movementSetHSpeed = new MovementSetHSpeed();
    private static MovementSetVSpeed movementSetVSpeed = new MovementSetVSpeed();

    static void setSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.ACTION_SET_SPEED.decode(action.getArgs(), movementSetSpeed);

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
        LibMovement.ACTION_SET_HSPEED.decode(action.getArgs(), movementSetHSpeed);

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
        LibMovement.ACTION_SET_VSPEED.decode(action.getArgs(), movementSetVSpeed);

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
}
