package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;

public class SetScore {

    private int score;
    private boolean relative;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    static class Definition extends ActionDefinition<SetScore> {
        Definition() {
            super("control_set_score", "Sets the score in the game", SetScore::new);

            addArgument(ArgumentType.INTEGER, "Score",
                    SetScore::getScore,
                    (o, v) -> o.setScore((Integer) v));

            addArgument(ArgumentType.BOOLEAN, "Relative",
                    SetScore::isRelative,
                    (o, v) -> o.setRelative((Boolean) v));
        }
    }
}
