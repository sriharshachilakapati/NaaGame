package com.naagame.core.action.movement;

import com.naagame.core.action.ActionDefinition;

public class LibMovement {
    public static final ActionDefinition<MovementSetSpeed> SET_SPEED = new MovementSetSpeed.Definition();
    public static final ActionDefinition<MovementSetHSpeed> SET_HSPEED = new MovementSetHSpeed.Definition();
    public static final ActionDefinition<MovementSetVSpeed> SET_VSPEED = new MovementSetVSpeed.Definition();
    public static final ActionDefinition<MovementSetPosition> SET_POSITION = new MovementSetPosition.Definition();
}
