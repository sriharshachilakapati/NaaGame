package com.naagame.core.resources;

import java.util.ArrayList;
import java.util.List;

public class NgmSprite implements IResource {
    private List<Frame> frames = new ArrayList<>();
    private String name;

    public NgmSprite(String name) {
        this.name = name;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    @Override
    public String getName() {
        return name;
    }

    public static class Frame {
        private NgmTexture texture;
        private int duration;

        public Frame(NgmTexture texture, int duration) {
            this.texture = texture;
            this.duration = duration;
        }

        public NgmTexture getTexture() {
            return texture;
        }

        public int getDuration() {
            return duration;
        }

        public void setTexture(NgmTexture texture) {
            this.texture = texture;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
