package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;

public class LibControl {
    public static final ActionDefinition<CreateInstance> CREATE_INSTANCE = new CreateInstance.Definition();
    public static final ActionDefinition<DestroyInstance> DESTROY_INSTANCE = new DestroyInstance.Definition();

    public static final ActionDefinition<PlaySound> PLAY_SOUND = new PlaySound.Definition();
    public static final ActionDefinition<StopSound> STOP_SOUND = new StopSound.Definition();
    public static final ActionDefinition<GotoScene> GOTO_SCENE = new GotoScene.Definition();
}
