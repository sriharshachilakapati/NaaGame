package com.naagame.player;

import com.naagame.core.resources.NgmEntity;
import com.shc.silenceengine.math.Quaternion;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.utils.MathUtils;

class LibDebugImpl {
    static void log(NgmEntity.Event.Action action, EntityInstance self) {
        NaaGamePlayer.logger.info(action.getArgs());
    }

    static void logProps(NgmEntity.Event.Action action, EntityInstance self) {
        EntityInstance instance;

        switch (action.getArgs()) {
            case "self":  instance = self;       break;
            case "other": instance = self.other; break;

            default:
                NaaGamePlayer.logger.warn("Unknown argument " + action.getArgs());
                return;
        }

        Quaternion q = instance.transformComponent.getRotation();

        float sin = +2f * (q.w * q.z + q.x * q.y);
        float cos = +1f - 2f * (q.y * q.y + q.z * q.z);
        float yaw = MathUtils.atan2(sin, cos);

        NaaGamePlayer.logger.info("Position: " + new Vector2(instance.transformComponent.getPosition()));
        NaaGamePlayer.logger.info("Rotation: " + yaw);
        NaaGamePlayer.logger.info("Speed: " + instance.speed);
        NaaGamePlayer.logger.info("Sprite: " + instance.ngmSprite.getName());
    }
}