package com.naagame.core.action.control;

import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.resources.NgmSound;

public class StopSound {
    private NgmSound sound;

    public NgmSound getSound() {
        return sound;
    }

    public void setSound(NgmSound sound) {
        this.sound = sound;
    }

    static class Definition extends ActionDefinition<StopSound> {
        Definition() {
            super("control_stop_sound", "Stops a currently playing sound", StopSound::new);

            addArgument(ArgumentType.SOUND, "Sound",
                    StopSound::getSound,
                    (o, v) -> o.setSound((NgmSound) v));
        }
    }
}
