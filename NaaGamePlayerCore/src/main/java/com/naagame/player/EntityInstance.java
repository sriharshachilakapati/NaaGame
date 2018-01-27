package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.Animation;
import com.shc.silenceengine.graphics.Sprite;
import com.shc.silenceengine.scene.Component;
import com.shc.silenceengine.scene.Entity;
import com.shc.silenceengine.scene.components.SpriteComponent;

class EntityInstance extends Entity {

    EntityInstance(float posX, float posY, String name) {
        transformComponent.setPosition(posX, posY);

        NgmEntity ngmEntity = NgmProject.find(NgmProject.entities, name);

        if (ngmEntity == null) {
            NaaGamePlayer.logger.error("Entity " + name + " is not defined in project file.");
            SilenceEngine.display.close();
            return;
        }

        NgmSprite ngmSprite = ngmEntity.getSprite();
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

        private Behaviour(NgmEntity ngmEntity) {
            this.ngmEntity = ngmEntity;
        }

        @Override
        protected void onCreate() {
            for (NgmEntity.Event event : ngmEntity.getEvents()) {
                if (event.getType() == NgmEntity.Event.Type.CREATE) {
                    interpret(event);
                }
            }
        }

        @Override
        protected void onUpdate(float elapsedTime) {
            for (NgmEntity.Event event : ngmEntity.getEvents()) {
                if (event.getType() == NgmEntity.Event.Type.UPDATE) {
                    interpret(event);
                }
            }
        }

        private void interpret(NgmEntity.Event event) {
            event.getActions().forEach(this::interpret);
        }

        private void interpret(NgmEntity.Event.Action action) {
            switch (action.getCode()) {
                case 0:
                    NaaGamePlayer.logger.info(action.getArgs());
                    break;

                default:
                    NaaGamePlayer.logger.warn("Unknown action code " + action.getCode());
                    break;
            }
        }
    }
}
