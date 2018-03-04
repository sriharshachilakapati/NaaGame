package com.naagame.player;

import com.naagame.core.action.debug.DebugLog;
import com.naagame.core.action.debug.DebugLogProps;
import com.naagame.core.action.debug.LibDebug;
import com.naagame.core.resources.NgmEntity;
import com.shc.silenceengine.math.Quaternion;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.utils.MathUtils;

class LibDebugImpl {
    private static DebugLog debugLog = new DebugLog();
    private static DebugLogProps debugLogProps = new DebugLogProps();

    static void log(NgmEntity.Event.Action action, EntityInstance self) {
        LibDebug.LOG.decode(action.getArgs(), debugLog);
        NaaGamePlayer.logger.info(debugLog.getMessage());
    }

    static void logProps(NgmEntity.Event.Action action, EntityInstance self) {
        LibDebug.LOG_PROPS.decode(action.getArgs(), debugLogProps);

        EntityInstance instance = null;

        switch (debugLogProps.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
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