package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmScene;
import com.naagame.core.resources.NgmTexture;
import com.shc.silenceengine.collision.broadphase.Grid;
import com.shc.silenceengine.collision.colliders.CollisionSystem2D;
import com.shc.silenceengine.core.GameState;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.DynamicRenderer;
import com.shc.silenceengine.graphics.IGraphicsDevice;
import com.shc.silenceengine.graphics.SceneRenderSystem;
import com.shc.silenceengine.graphics.cameras.OrthoCam;
import com.shc.silenceengine.graphics.opengl.GLContext;
import com.shc.silenceengine.graphics.opengl.Primitive;
import com.shc.silenceengine.graphics.opengl.Texture;
import com.shc.silenceengine.scene.Scene;

import java.util.List;
import java.util.stream.Collectors;

public class SceneState extends GameState {
    static SceneState instance;

    private final NgmScene ngmScene;

    Scene scene;

    private OrthoCam camera;
    private CollisionSystem2D collider;
    private OrthoCam bgCam;

    SceneState(String scene) {
        instance = this;
        ngmScene = NgmProject.find(NgmProject.scenes, scene);

        if (ngmScene == null) {
            NaaGamePlayer.logger.error("Scene " + scene + " is not present in project file");
            SilenceEngine.display.close();
            return;
        }

        SilenceEngine.display.setSize(ngmScene.getWidth(), ngmScene.getHeight());
        SilenceEngine.display.centerOnScreen();
    }

    @Override
    public void onEnter() {
        camera = new OrthoCam();
        bgCam = new OrthoCam();

        scene = new Scene();
        scene.registerRenderSystem(new SceneRenderSystem());

        collider = new CollisionSystem2D(new Grid(ngmScene.getWidth(), ngmScene.getHeight(), 128, 128));
        scene.registerUpdateSystem(collider);

        loadScene();
        resized();
    }

    private void loadScene() {
        ngmScene.getEntities().forEach(instance -> {
            EntityInstance entity = new EntityInstance(instance.getPosX(), instance.getPosY(),
                    instance.getObject().getName());
            scene.addEntity(entity);
        });

        for (NgmEntity entity : NgmProject.entities) {
            List<NgmEntity.Event> collisionEvents = entity.getEvents().stream()
                    .filter(event -> event.getType() == NgmEntity.Event.Type.COLLISION)
                    .collect(Collectors.toList());

            for (NgmEntity.Event collisionEvent : collisionEvents) {
                String selfType = entity.getName();
                String collType = collisionEvent.getArgs();

                collider.register(Resources.collisionTags.get(selfType), Resources.collisionTags.get(collType));
            }
        }
    }

    @Override
    public void update(float delta) {
        scene.update(delta);
    }

    @Override
    public void render(float delta) {
        bgCam.apply();

        for (NgmScene.Instance<NgmBackground> bgInstance : ngmScene.getBackgrounds()) {
            NgmBackground ngmBackground = bgInstance.getObject();

            if (ngmBackground == null) {
                continue;
            }

            NgmTexture bgTexture = ngmBackground.getTexture();

            if (bgTexture == null) {
                continue;
            }

            renderBackground(Resources.textures.get(bgTexture.getName()));
        }

        Texture.EMPTY.bind();
        camera.apply();
        scene.render(delta);
    }

    private void renderBackground(Texture texture) {
        DynamicRenderer renderer = IGraphicsDevice.Renderers.dynamic;

        texture.bind();
        renderer.begin(Primitive.TRIANGLE_FAN);
        {
            renderer.vertex(0, 0);
            renderer.texCoord(0, 0);
            renderer.vertex(1, 0);
            renderer.texCoord(1, 0);
            renderer.vertex(1, 1);
            renderer.texCoord(1, 1);
            renderer.vertex(0, 1);
            renderer.texCoord(0, 1);
        }
        renderer.end();
    }

    @Override
    public void resized() {
        camera.initProjection(SilenceEngine.display.getWidth(), SilenceEngine.display.getHeight());
        bgCam.initProjection(1, 1);
        GLContext.viewport(0, 0, SilenceEngine.display.getWidth(), SilenceEngine.display.getHeight());
    }
}
