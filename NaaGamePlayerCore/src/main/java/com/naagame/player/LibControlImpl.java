package com.naagame.player;

import com.naagame.core.action.control.CreateInstance;
import com.naagame.core.action.control.LibControl;
import com.naagame.core.resources.NgmEntity;

public class LibControlImpl {
    private static CreateInstance createInstance = new CreateInstance();

    static void createInstance(NgmEntity.Event.Action action, EntityInstance self) {
        LibControl.CREATE_INSTANCE.decode(action.getArgs(), createInstance);

        EntityInstance entityInstance = new EntityInstance(createInstance.getPosX(),
                createInstance.getPosY(),
                createInstance.getEntity());

        SceneState.instance.scene.addEntity(entityInstance);
    }
}
