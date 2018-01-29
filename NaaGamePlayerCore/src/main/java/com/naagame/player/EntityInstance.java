package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import com.naagame.lib.Debug;
import com.naagame.lib.Movement;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.Animation;
import com.shc.silenceengine.graphics.Sprite;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.input.Mouse;
import com.shc.silenceengine.math.Quaternion;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.scene.Component;
import com.shc.silenceengine.scene.Entity;
import com.shc.silenceengine.scene.components.SpriteComponent;
import com.shc.silenceengine.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

        private EntityInstance self;

        private NgmEntity.Event createEvent;
        private NgmEntity.Event updateEvent;
        private NgmEntity.Event destroyEvent;

        private List<NgmEntity.Event> inputEvents;

        private Behaviour(NgmEntity ngmEntity) {
            inputEvents = new ArrayList<>();

            ngmEntity.getEvents().forEach(event -> {
                switch (event.getType()) {
                    case CREATE:
                        if (createEvent != null) {
                            NaaGamePlayer.logger.warn("Found more than one create events in " + ngmEntity.getName());
                        }
                        createEvent = event;
                        break;

                    case UPDATE:
                        if (updateEvent != null) {
                            NaaGamePlayer.logger.warn("Found more than one update events in " + ngmEntity.getName());
                        }
                        updateEvent = event;
                        break;

                    case KEY_DOWN:
                    case KEY_UP:
                    case KEY_TAP:
                    case MOUSE_DOWN:
                    case MOUSE_UP:
                    case MOUSE_TAP:
                        inputEvents.add(event);
                        break;

                    case DESTROY:
                        if (destroyEvent != null) {
                            NaaGamePlayer.logger.warn("Found more than one destroy events in " + ngmEntity.getName());
                        }
                        destroyEvent = event;
                        break;

                    default:
                        NaaGamePlayer.logger.warn("Event " + event.getType() + " is not implemented yet");
                }
            });
        }

        @Override
        protected void onCreate() {
            self = (EntityInstance) entity;

            if (createEvent != null) {
                interpret(createEvent);
            }
        }

        @Override
        protected void onUpdate(float elapsedTime) {
            self.other = null;

            if (updateEvent != null) {
                interpret(updateEvent);
            }

            inputEvents.forEach(event -> {
                Function<Integer, Boolean> condition = null;

                switch (event.getType()) {
                    case KEY_DOWN:   condition = Keyboard::isKeyDown;   break;
                    case KEY_UP:     condition = Keyboard::isKeyUp;     break;
                    case KEY_TAP:    condition = Keyboard::isKeyTapped; break;
                    case MOUSE_DOWN: condition = Mouse::isButtonDown;   break;
                    case MOUSE_UP:   condition = Mouse::isButtonUp;     break;
                    case MOUSE_TAP:  condition = Mouse::isButtonTapped; break;

                    default:
                        NaaGamePlayer.logger.warn("Input event " + event.getType() + " is not yet implemented");
                }

                if (condition != null) {
                    int argument = Integer.parseInt(event.getArgs());

                    if (condition.apply(argument)) {
                        interpret(event);
                    }
                }
            });

            transformComponent.translate(self.speed);
        }

        @Override
        protected void onDestroyed() {
            if (destroyEvent != null) {
                interpret(destroyEvent);
            }
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
