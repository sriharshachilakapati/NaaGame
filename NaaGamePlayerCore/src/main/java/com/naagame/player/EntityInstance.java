package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import com.naagame.lib.Debug;
import com.naagame.lib.Movement;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.Animation;
import com.shc.silenceengine.graphics.Sprite;
import com.shc.silenceengine.math.Quaternion;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.scene.Component;
import com.shc.silenceengine.scene.Entity;
import com.shc.silenceengine.scene.components.SpriteComponent;
import com.shc.silenceengine.utils.MathUtils;

class EntityInstance extends Entity {

    private EntityInstance other;

    private NgmSprite ngmSprite;
    private Vector2 speed;

    EntityInstance(float posX, float posY, String name) {
        transformComponent.setPosition(posX, posY);
        speed = new Vector2();

        NgmEntity ngmEntity = NgmProject.find(NgmProject.entities, name);

        if (ngmEntity == null) {
            NaaGamePlayer.logger.error("Entity " + name + " is not defined in project file.");
            SilenceEngine.display.close();
            return;
        }

        ngmSprite = ngmEntity.getSprite();
        Animation animation = Resources.animations.get(ngmSprite.getName());

        Sprite sprite = new Sprite(animation);
        sprite.setEndCallback(sprite::start);
        sprite.start();

        SpriteComponent spriteComponent = new SpriteComponent(sprite);
        addComponent(spriteComponent);
        addComponent(new Behaviour(ngmEntity));
    }

    private static class Behaviour extends Component {

        private NgmEntity ngmEntity;
        private EntityInstance self;

        private Behaviour(NgmEntity ngmEntity) {
            this.ngmEntity = ngmEntity;
        }

        @Override
        protected void onCreate() {
            self = (EntityInstance) entity;

            for (NgmEntity.Event event : ngmEntity.getEvents()) {
                if (event.getType() == NgmEntity.Event.Type.CREATE) {
                    interpret(event);
                }
            }
        }

        @Override
        protected void onUpdate(float elapsedTime) {
            self.other = null;

            for (NgmEntity.Event event : ngmEntity.getEvents()) {
                if (event.getType() == NgmEntity.Event.Type.UPDATE) {
                    interpret(event);
                }
            }

            transformComponent.translate(self.speed);
        }

        private void interpret(NgmEntity.Event event) {
            event.getActions().forEach(this::interpret);
        }

        private void interpret(NgmEntity.Event.Action action) {
            switch (action.getCode()) {
                case Debug.ACTION_LOG:
                    NaaGamePlayer.logger.info(action.getArgs());
                    break;

                case Debug.ACTION_LOG_PROPS:
                    switch (action.getArgs()) {
                        case "self": logProps(self); break;
                        case "other":
                            if (self.other == null) {
                                NaaGamePlayer.logger.warn("Argument other can only be used in collision event");
                                break;
                            }

                            logProps(self.other);
                            break;
                        default:
                            NaaGamePlayer.logger.warn("Unknown argument " + action.getArgs());
                    }
                    break;

                case Movement.ACTION_SET_XSPEED:
                    setSpeed(action.getArgs(), true, false);
                    break;

                case Movement.ACTION_SET_YSPEED:
                    setSpeed(action.getArgs(), false, true);
                    break;

                case Movement.ACTION_SET_SPEED:
                    setSpeed(action.getArgs(), true, true);
                    break;

                default:
                    NaaGamePlayer.logger.warn("Unknown action code " + action.getCode());
                    break;
            }
        }

        private void logProps(EntityInstance entity) {
            Quaternion q = entity.transformComponent.getRotation();

            float sin = +2f * (q.w * q.z + q.x * q.y);
            float cos = +1f - 2f * (q.y * q.y + q.z * q.z);
            float yaw = MathUtils.atan2(sin, cos);

            NaaGamePlayer.logger.info("Position: " + new Vector2(entity.transformComponent.getPosition()));
            NaaGamePlayer.logger.info("Rotation: " + yaw);
            NaaGamePlayer.logger.info("Speed: " + entity.speed);
            NaaGamePlayer.logger.info("Sprite: " + entity.ngmSprite.getName());
        }

        private void setSpeed(String actionArgs, boolean setX, boolean setY) {
            String[] parts = actionArgs.split(";");

            EntityInstance instance;

            switch (parts[0]) {
                case "self":
                    instance = self;
                    break;
                case "other":
                    instance = self.other;
                    break;

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
    }
}
