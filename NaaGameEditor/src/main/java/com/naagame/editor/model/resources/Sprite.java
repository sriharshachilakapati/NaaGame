package com.naagame.editor.model.resources;

import java.util.ArrayList;
import java.util.List;

public class Sprite implements IResource {
    private final List<Frame> frames = new ArrayList<>();
    private String name;

    public Sprite(String name) {
        this.name = name;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    @Override
    public String getName() {
        return name;
    }

    public static class Frame {
        private Texture texture;
        private int duration;

        public Texture getTexture() {
            return texture;
        }

        public int getDuration() {
            return duration;
        }
    }
}
