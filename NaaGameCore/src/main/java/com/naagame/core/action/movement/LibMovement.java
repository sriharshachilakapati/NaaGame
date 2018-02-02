package com.naagame.core.action.movement;

import com.naagame.core.action.ActionDefinition;

public class LibMovement {
    public static final ActionDefinition<MovementSetSpeed> ACTION_SET_SPEED = new MovementSetSpeed.Definition();
    public static final ActionDefinition<MovementSetHSpeed> ACTION_SET_HSPEED = new MovementSetHSpeed.Definition();
    public static final ActionDefinition<MovementSetVSpeed> ACTION_SET_VSPEED = new MovementSetVSpeed.Definition();
    public static final ActionDefinition<MovementSetPosition> ACTION_SET_POSITION = new MovementSetPosition.Definition();
}
