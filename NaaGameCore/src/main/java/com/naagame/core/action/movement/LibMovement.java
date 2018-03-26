package com.naagame.core.action.movement;

import com.naagame.core.action.ActionDefinition;

public class LibMovement {
    public static final ActionDefinition<SetSpeed> SET_SPEED = new SetSpeed.Definition();
    public static final ActionDefinition<SetHSpeed> SET_HSPEED = new SetHSpeed.Definition();
    public static final ActionDefinition<SetVSpeed> SET_VSPEED = new SetVSpeed.Definition();
    public static final ActionDefinition<SetPosition> SET_POSITION = new SetPosition.Definition();
    public static final ActionDefinition<Bounce> BOUNCE = new Bounce.Definition();
    public static final ActionDefinition<SetPositionRelativeTo> SET_POSITION_RELATIVE_TO = new SetPositionRelativeTo.Definition();
}
