package com.naagame.player;

import com.naagame.core.action.control.LibControl;
import com.naagame.core.action.debug.LibDebug;
import com.naagame.core.action.movement.LibMovement;
import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.Animation;
import com.shc.silenceengine.graphics.Sprite;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.input.Mouse;
import com.shc.silenceengine.math.Vector2;
import com.shc.silenceengine.math.geom2d.Polygon;
import com.shc.silenceengine.math.geom2d.Rectangle;
import com.shc.silenceengine.scene.Component;
import com.shc.silenceengine.scene.Entity;
import com.shc.silenceengine.scene.components.CollisionComponent2D;
import com.shc.silenceengine.scene.components.SpriteComponent;
import com.shc.silenceengine.utils.functional.BiCallback;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class EntityInstance extends Entity {
    static final Map<String, Integer> countMap = new HashMap<>();

    EntityInstance other;

    NgmSprite ngmSprite;
    Vector2 speed;

    CollisionComponent2D collisionComponent;

    private String name;

    private Behaviour behaviour;

    EntityInstance(float posX, float posY, String name) {
        this.name = name;

        transformComponent.setPosition(posX, posY);
        speed = new Vector2();

        NgmEntity ngmEntity = NgmProject.find(NgmProject.entities, name);

        if (ngmEntity == null) {
            NaaGamePlayer.logger.error("Entity " + name + " is not defined in project file.");
            SilenceEngine.display.close();
            return;
        }

        ngmSprite = ngmEntity.getSprite();

        behaviour = new Behaviour(ngmEntity);

        if (ngmSprite != null) {
            Animation animation = Resources.animations.get(ngmSprite.getName());

            Sprite sprite = new Sprite(animation);
            sprite.setEndCallback(sprite::start);
            sprite.start();

            addComponent(new SpriteComponent(sprite));

            float boundsWidth = 0;
            float boundsHeight = 0;

            for (int i = 0; i < animation.size(); i++) {
                boundsWidth += animation.getFrameTexture(i).getWidth();
                boundsHeight += animation.getFrameTexture(i).getHeight();
            }

            boundsWidth /= animation.size();
            boundsHeight /= animation.size();

            Rectangle bounds = new Rectangle(boundsWidth, boundsHeight);

            addComponent(collisionComponent = new CollisionComponent2D(Resources.collisionTags.get(name),
                    bounds.createPolygon(),
                    behaviour::onCollision));
        }

        addComponent(behaviour);
    }

    @Override
    public void destroy() {
        if (!isDestroyed()) {
            behaviour.handleDestroy();
            super.destroy();
        }
    }

    private static class Behaviour extends Component {

        private EntityInstance self;

        private NgmEntity.Event createEvent;
        private NgmEntity.Event updateEvent;
        private NgmEntity.Event destroyEvent;
        private NgmEntity.Event outOfBoundsEvent;
        private NgmEntity.Event noMoreLivesEvent;

        private List<NgmEntity.Event> inputEvents;
        private List<NgmEntity.Event> collisionEvents;
        private List<NgmEntity.Event> noneExistEvents;

        private Polygon polygon;

        private static Map<String, BiCallback<NgmEntity.Event.Action, EntityInstance>> actionExecutors;

        static {
            actionExecutors = new HashMap<>();

            actionExecutors.put(LibDebug.LOG.getCode(), LibDebugImpl::log);
            actionExecutors.put(LibDebug.LOG_PROPS.getCode(), LibDebugImpl::logProps);

            actionExecutors.put(LibMovement.SET_SPEED.getCode(), LibMovementImpl::setSpeed);
            actionExecutors.put(LibMovement.SET_HSPEED.getCode(), LibMovementImpl::setHSpeed);
            actionExecutors.put(LibMovement.SET_VSPEED.getCode(), LibMovementImpl::setVSpeed);
            actionExecutors.put(LibMovement.SET_POSITION.getCode(), LibMovementImpl::setPosition);
            actionExecutors.put(LibMovement.BOUNCE.getCode(), LibMovementImpl::bounce);

            actionExecutors.put(LibControl.CREATE_INSTANCE.getCode(), LibControlImpl::createInstance);
            actionExecutors.put(LibControl.DESTROY_INSTANCE.getCode(), LibControlImpl::destroyInstance);
            actionExecutors.put(LibControl.PLAY_SOUND.getCode(), LibControlImpl::playSound);
            actionExecutors.put(LibControl.STOP_SOUND.getCode(), LibControlImpl::stopSound);
            actionExecutors.put(LibControl.GOTO_SCENE.getCode(), LibControlImpl::gotoScene);
            actionExecutors.put(LibControl.SET_SCORE.getCode(), LibControlImpl::setScore);
            actionExecutors.put(LibControl.SET_LIVES.getCode(), LibControlImpl::setLives);
        }

        private Behaviour(NgmEntity ngmEntity) {
            inputEvents = new ArrayList<>();

            createEvent = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.CREATE)
                    .findFirst().orElse(null);

            updateEvent = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.UPDATE)
                    .findFirst().orElse(null);

            outOfBoundsEvent = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.OUT_OF_BOUNDS)
                    .findFirst().orElse(null);

            destroyEvent = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.DESTROY)
                    .findFirst().orElse(null);

            collisionEvents = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.COLLISION)
                    .collect(Collectors.toList());

            noneExistEvents = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.NONE_EXISTS)
                    .collect(Collectors.toList());

            noMoreLivesEvent = ngmEntity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.NO_MORE_LIVES)
                    .findFirst().orElse(null);

            ngmEntity.getEvents().stream().filter(this::isInputEvent).forEach(inputEvents::add);
        }

        private boolean isInputEvent(NgmEntity.Event event) {
            switch (event.getType()) {
                case KEY_DOWN:   return true;
                case KEY_UP:     return true;
                case KEY_TAP:    return true;
                case MOUSE_DOWN: return true;
                case MOUSE_UP:   return true;
                case MOUSE_TAP:  return true;
                default:         return false;
            }
        }

        @Override
        protected void onCreate() {
            self = (EntityInstance) entity;
            countMap.put(self.name, countMap.getOrDefault(self.name, 0) + 1);

            if (createEvent != null) {
                interpret(createEvent);
            }

            CollisionComponent2D collisionComponent = entity.getComponent(CollisionComponent2D.class);

            if (collisionComponent != null) {
                polygon = collisionComponent.polygon;
            }
        }

        @Override
        protected void onUpdate(float elapsedTime) {
            self.other = null;

            if (updateEvent != null) {
                interpret(updateEvent);
            }

            if (outOfBoundsEvent != null && polygon != null && !SceneState.instance.bounds.intersects(polygon)) {
                interpret(outOfBoundsEvent);
            }

            for (NgmEntity.Event event : noneExistEvents) {
                if (countMap.getOrDefault(event.getArgs(), 0) == 0) {
                    interpret(event);
                }
            }

            if (SceneState.lives <= 0 && noMoreLivesEvent != null) {
                interpret(noMoreLivesEvent);
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

        void handleDestroy() {
            if (destroyEvent != null) {
                interpret(destroyEvent);
            }
            countMap.put(self.name, countMap.getOrDefault(self.name, 1) - 1);
        }

        private void interpret(NgmEntity.Event event) {
            event.getActions().forEach(this::interpret);
        }

        private void interpret(NgmEntity.Event.Action action) {
            actionExecutors.getOrDefault(action.getCode(), this::unknownAction).invoke(action, self);
        }

        private void unknownAction(NgmEntity.Event.Action action, EntityInstance self) {
            NaaGamePlayer.logger.warn("Unknown action code " + action.getCode());
        }

        private void onCollision(CollisionComponent2D otherComponent) {
            self.other = (EntityInstance) otherComponent.getEntity();

            collisionEvents.stream()
                    .filter(event -> event.getArgs().equals(self.other.name))
                    .findFirst()
                    .ifPresent(this::interpret);
        }
    }
}
