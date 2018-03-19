package com.naagame.player;

import com.naagame.core.action.control.CreateInstance;
import com.naagame.core.action.control.DestroyInstance;
import com.naagame.core.action.control.LibControl;
import com.naagame.core.resources.NgmEntity;
import com.shc.silenceengine.utils.TaskManager;

public class LibControlImpl {
    private static CreateInstance createInstance = new CreateInstance();
    private static DestroyInstance destroyInstance = new DestroyInstance();

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
}
