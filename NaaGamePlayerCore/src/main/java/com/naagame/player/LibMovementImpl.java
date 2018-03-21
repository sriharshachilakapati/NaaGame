package com.naagame.player;

import com.naagame.core.action.movement.*;
import com.naagame.core.resources.NgmEntity;
import com.shc.silenceengine.math.Vector3;
import com.shc.silenceengine.math.geom2d.Rectangle;

class LibMovementImpl {
    private static final SetSpeed setSpeed = new SetSpeed();
    private static final SetHSpeed setHSpeed = new SetHSpeed();
    private static final SetVSpeed setVSpeed = new SetVSpeed();
    private static final SetPosition setPosition = new SetPosition();
    private static final Bounce bounce = new Bounce();

    static void setSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_SPEED.decode(action.getArgs(), setSpeed);

        EntityInstance instance = null;

        switch (setSpeed.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        float hSpeed = setSpeed.getHSpeed();
        float vSpeed = setSpeed.getVSpeed();

        if (setSpeed.isRelative()) {
            hSpeed += instance.speed.x;
            vSpeed += instance.speed.y;
        }

        instance.speed.set(hSpeed, vSpeed);
    }

    static void setHSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_HSPEED.decode(action.getArgs(), setHSpeed);

        EntityInstance instance = null;

        switch (setHSpeed.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        if (setHSpeed.isRelative()) {
            instance.speed.x += setHSpeed.getHSpeed();
        } else {
            instance.speed.x = setHSpeed.getHSpeed();
        }
    }

    static void setVSpeed(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_VSPEED.decode(action.getArgs(), setVSpeed);

        EntityInstance instance = null;

        switch (setVSpeed.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        if (setVSpeed.isRelative()) {
            instance.speed.y += setVSpeed.getVSpeed();
        } else {
            instance.speed.y = setVSpeed.getVSpeed();
        }
    }

    static void setPosition(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.SET_POSITION.decode(action.getArgs(), setPosition);

        EntityInstance instance = null;

        switch (setPosition.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        float posX = setPosition.getPosX();
        float posY = setPosition.getPosY();

        Vector3 selfPos = self.transformComponent.getPosition();

        if (setPosition.isRelative()) {
            posX += selfPos.x;
            posY += selfPos.y;
        }

        instance.transformComponent.setPosition(posX, posY);
    }

    static void bounce(NgmEntity.Event.Action action, EntityInstance self) {
        LibMovement.BOUNCE.decode(action.getArgs(), bounce);

        switch (bounce.getTarget()) {
            case SELF:  bounce(self, self.other); break;
            case OTHER: bounce(self.other, self); break;
        }
    }

    private static void bounce(EntityInstance a, EntityInstance b) {
        Rectangle aRect = a.collisionComponent.polygon.getBounds();
        Rectangle bRect = b.collisionComponent.polygon.getBounds();

        float iw = aRect.getIntersectionWidth(bRect);
        float ih = aRect.getIntersectionHeight(bRect);

        if (iw > ih) {
            a.speed.y *= -1;
        }
        else if (ih > iw) {
            a.speed.x *= -1;
        }
        else {
            a.speed.x *= -1;
            a.speed.y *= -1;
        }
    }
}
