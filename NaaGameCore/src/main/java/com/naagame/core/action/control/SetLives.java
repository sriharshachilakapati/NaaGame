package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;

public class SetLives {

    private int lives;
    private boolean relative;

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    static class Definition extends ActionDefinition<SetLives> {
        Definition() {
            super("control_set_lives", "Sets the lives in the game", SetLives::new);

            addArgument(ArgumentType.INTEGER, "Lives",
                    SetLives::getLives,
                    (o, v) -> o.setLives((Integer) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    SetLives::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
