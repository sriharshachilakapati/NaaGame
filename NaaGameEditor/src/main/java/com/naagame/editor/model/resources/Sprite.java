package com.naagame.editor.model.resources;

import java.util.ArrayList;
import java.util.List;

public class Sprite implements IResource {
    private List<Frame> frames = new ArrayList<>();
    private String name;

    public Sprite(String name) {
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
        private Texture texture;
        private int duration;

        public Frame(Texture texture, int duration) {
            this.texture = texture;
            this.duration = duration;
        }

        public Texture getTexture() {
            return texture;
        }

        public int getDuration() {
            return duration;
        }

        public void setTexture(Texture texture) {
            this.texture = texture;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
