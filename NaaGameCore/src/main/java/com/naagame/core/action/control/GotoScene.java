package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.resources.NgmScene;

public class GotoScene {
    private NgmScene scene;

    public NgmScene getScene() {
        return scene;
    }

    public void setScene(NgmScene scene) {
        this.scene = scene;
    }

    static class Definition extends ActionDefinition<GotoScene> {
        Definition() {
            super("control_goto_scene", "Changes the current scene with a new one", GotoScene::new);

            addArgument(ArgumentType.SCENE, "Scene",
                    GotoScene::getScene,
                    (o, v) -> o.setScene((NgmScene) v));
        }
    }
}
