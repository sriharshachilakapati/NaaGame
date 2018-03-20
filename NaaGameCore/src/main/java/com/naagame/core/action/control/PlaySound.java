package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.resources.NgmSound;

public class PlaySound {
    private NgmSound sound;

    public NgmSound getSound() {
        return sound;
    }

    public void setSound(NgmSound sound) {
        this.sound = sound;
    }

    static class Definition extends ActionDefinition<PlaySound> {
        Definition() {
            super("control_play_sound", "Plays a sound once", PlaySound::new);

            addArgument(ArgumentType.SOUND, "Sound",
                    PlaySound::getSound,
                    (o, v) -> o.setSound((NgmSound) v));
        }
    }
}
