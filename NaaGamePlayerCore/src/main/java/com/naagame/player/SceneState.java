package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmScene;
import com.shc.silenceengine.collision.broadphase.Grid;
import com.shc.silenceengine.collision.colliders.CollisionSystem2D;
import com.shc.silenceengine.core.GameState;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.graphics.SceneRenderSystem;
import com.shc.silenceengine.graphics.cameras.OrthoCam;
import com.shc.silenceengine.graphics.opengl.GLContext;
import com.shc.silenceengine.graphics.opengl.Texture;
import com.shc.silenceengine.math.geom2d.Polygon;
import com.shc.silenceengine.math.geom2d.Rectangle;
import com.shc.silenceengine.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SceneState extends GameState {
    static SceneState instance;
    static int score;
    static int lives;

    private final NgmScene ngmScene;

    Scene scene;
    Polygon bounds;

    private OrthoCam camera;
    private CollisionSystem2D collider;
    private OrthoCam bgCam;

    private List<BackgroundInstance> backgroundInstances;

    SceneState(String scene) {
        instance = this;
        ngmScene = NgmProject.find(NgmProject.scenes, scene);

        if (ngmScene == null) {
            NaaGamePlayer.logger.error("Scene " + scene + " is not present in project file");
            SilenceEngine.display.close();
            return;
        }

        bounds = new Rectangle(ngmScene.getWidth(), ngmScene.getHeight()).createPolygon();
        EntityInstance.countMap.clear();

        SilenceEngine.display.setSize(ngmScene.getWidth(), ngmScene.getHeight());
        SilenceEngine.display.centerOnScreen();
    }

    @Override
    public void onEnter() {
        camera = new OrthoCam();
        bgCam = new OrthoCam();
        backgroundInstances = new ArrayList<>();

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

        for (NgmScene.Instance<NgmBackground> bgInstance : ngmScene.getBackgrounds()) {
            NgmBackground background = bgInstance.getObject();

            if (background == null || background.getTexture() == null) {
                continue;
            }

            backgroundInstances.add(new BackgroundInstance(bgInstance));
        }
    }

    @Override
    public void update(float delta) {
        scene.update(delta);
        backgroundInstances.forEach(BackgroundInstance::update);

        SilenceEngine.display.setTitle("NaaGamePlayer | SilenceEngine " + SilenceEngine.getVersionString() +
                " | Score: " + score + " | Lives: " + lives);
    }

    @Override
    public void render(float delta) {
        bgCam.apply();
        backgroundInstances.forEach(BackgroundInstance::render);
        Texture.EMPTY.bind();

        camera.apply();
        scene.render(delta);
    }

    @Override
    public void resized() {
        camera.initProjection(ngmScene.getWidth(), ngmScene.getHeight());
        bgCam.initProjection(1, 1);
        GLContext.viewport(0, 0, SilenceEngine.display.getWidth(), SilenceEngine.display.getHeight());
    }
}
