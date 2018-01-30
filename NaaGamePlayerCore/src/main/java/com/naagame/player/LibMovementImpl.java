package com.naagame.player;

import com.naagame.core.resources.NgmEntity;

class LibMovementImpl {
    private static void setSpeed(NgmEntity.Event.Action action, EntityInstance self, boolean setX, boolean setY) {
        String[] parts = action.getArgs().split(";");

        EntityInstance instance;

        switch (parts[0]) {
            case "self":  instance = self;       break;
            case "other": instance = self.other; break;

            default:
                NaaGamePlayer.logger.warn("Unknown entity argument " + parts[0]);
                return;
        }

        if (setX && setY) {
            float speedX = Float.parseFloat(parts[1]);
            float speedY = Float.parseFloat(parts[2]);

            instance.speed.set(speedX, speedY);
        } else {
            float speed = Float.parseFloat(parts[1]);

            if (setX) {
                instance.speed.x = speed;
            } else if (setY) {
                instance.speed.y = speed;
            }
        }
    }

    static void setSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        setSpeed(action, self, true, true);
    }

    static void setXSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        setSpeed(action, self, true, false);
    }

    static void setYSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        setSpeed(action, self, false, true);
    }
}
