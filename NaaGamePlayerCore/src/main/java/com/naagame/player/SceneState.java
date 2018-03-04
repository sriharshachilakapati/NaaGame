package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmScene;
import com.shc.silenceengine.collision.colliders.CollisionSystem2D;
import com.shc.silenceengine.core.GameState;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.SceneRenderSystem;
import com.shc.silenceengine.graphics.cameras.OrthoCam;
import com.shc.silenceengine.graphics.opengl.GLContext;
import com.shc.silenceengine.scene.Scene;

public class SceneState extends GameState {
    static SceneState instance;

    private final NgmScene ngmScene;

    Scene scene;
    private OrthoCam camera;

    SceneState(String scene) {
        instance = this;
        ngmScene = NgmProject.find(NgmProject.scenes, scene);

        if (ngmScene == null) {
            NaaGamePlayer.logger.error("Scene " + scene + " is not present in project file");
            SilenceEngine.display.close();
        }
    }

    @Override
    public void onEnter() {
        camera = new OrthoCam();

        scene = new Scene();
        scene.registerRenderSystem(new SceneRenderSystem());

        CollisionSystem2D collider = new CollisionSystem2D();
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
    }

    @Override
    public void update(float delta) {
        scene.update(delta);
    }

    @Override
    public void render(float delta) {
        camera.apply();
        scene.render(delta);
    }

    @Override
    public void resized() {
        camera.initProjection(SilenceEngine.display.getWidth(), SilenceEngine.display.getHeight());
        GLContext.viewport(0, 0, SilenceEngine.display.getWidth(), SilenceEngine.display.getHeight());
    }
}
