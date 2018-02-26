package com.naagame.core.resources;

public class NgmSound implements IResource {
    private String source = "";
    private String name;

    private boolean loop;
    private boolean playOnStart;

    public NgmSound(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isPlayOnStart() {
        return playOnStart;
    }

    public void setPlayOnStart(boolean playOnStart) {
        this.playOnStart = playOnStart;
    }
}
