package com.naagame.player;

import com.naagame.core.action.control.CreateInstance;
import com.naagame.core.action.control.DestroyInstance;
import com.naagame.core.action.control.LibControl;
import com.naagame.core.resources.NgmEntity;

public class LibControlImpl {
    private static CreateInstance createInstance = new CreateInstance();
    private static DestroyInstance destroyInstance = new DestroyInstance();

    static void createInstance(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.CREATE_INSTANCE.decode(action.getArgs(), createInstance);

        EntityInstance entityInstance = new EntityInstance(createInstance.getPosX(),
                createInstance.getPosY(),
                createInstance.getEntity());

        SceneState.instance.scene.addEntity(entityInstance);
    }

    static void destroyInstance(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.DESTROY_INSTANCE.decode(action.getArgs(), destroyInstance);

        EntityInstance instance = null;

        switch (destroyInstance.getTarget()) {
            case SELF:  instance = self;       break;
            case OTHER: instance = self.other; break;
        }

        instance.destroy();
    }
}
