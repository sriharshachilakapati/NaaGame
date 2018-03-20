package com.naagame.player;

import com.naagame.core.action.control.*;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSound;
import com.shc.silenceengine.utils.TaskManager;

public class LibControlImpl {
    private static CreateInstance createInstance = new CreateInstance();
    private static DestroyInstance destroyInstance = new DestroyInstance();

    private static PlaySound playSound = new PlaySound();
    private static StopSound stopSound = new StopSound();
    private static GotoScene gotoScene = new GotoScene();

    private static SetScore setScore = new SetScore();
    private static SetLives setLives = new SetLives();

    static void createInstance(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.CREATE_INSTANCE.decode(action.getArgs(), createInstance);

        float posX = createInstance.getPosX();
        float posY = createInstance.getPosY();

        if (createInstance.isRelative()) {
            posX += self.transformComponent.getPosition().x;
            posY += self.transformComponent.getPosition().y;
        }

        EntityInstance entityInstance = new EntityInstance(posX, posY, createInstance.getEntity().getName());

        SceneState.instance.scene.addEntity(entityInstance);
    }

    static void destroyInstance(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.DESTROY_INSTANCE.decode(action.getArgs(), destroyInstance);

        EntityInstance instance = null;

        switch (destroyInstance.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        EntityInstance finalInstance = instance;
        TaskManager.runOnUpdate(finalInstance::destroy);
    }

    static void playSound(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.PLAY_SOUND.decode(action.getArgs(), playSound);

        NgmSound sound = playSound.getSound();
        Resources.sounds.get(sound.getName()).play(sound.isLoop());
    }

    static void stopSound(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.STOP_SOUND.decode(action.getArgs(), stopSound);
        Resources.sounds.get(stopSound.getSound().getName()).stop();
    }

    static void gotoScene(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.GOTO_SCENE.decode(action.getArgs(), gotoScene);
        NaaGamePlayer.instance.setGameState(new SceneState(gotoScene.getScene().getName()));
    }

    static void setScore(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.SET_SCORE.decode(action.getArgs(), setScore);

        if (setScore.isRelative()) {
            SceneState.score += setScore.getScore();
        } else {
            SceneState.score = setScore.getScore();
        }

        SceneState.score = Math.max(SceneState.score, 0);
    }

    static void setLives(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.SET_LIVES.decode(action.getArgs(), setLives);

        if (setLives.isRelative()) {
            SceneState.lives += setLives.getLives();
        } else {
            SceneState.lives = setLives.getLives();
        }

        SceneState.lives = Math.max(SceneState.lives, 0);
    }
}
