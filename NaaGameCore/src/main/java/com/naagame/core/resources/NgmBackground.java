package com.naagame.core.resources;

public class NgmBackground implements IResource {
    private NgmTexture texture;

    private float hSpeed;
    private float vSpeed;
    private String name;

    public NgmBackground(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public NgmTexture getTexture() {
        return texture;
    }

    public float getHSpeed() {
        return hSpeed;
    }

    public float getVSpeed() {
        return vSpeed;
    }

    public void setTexture(NgmTexture texture) {
        this.texture = texture;
    }

    public void setHSpeed(float hSpeed) {
        this.hSpeed = hSpeed;
    }

    public void setVSpeed(float vSpeed) {
        this.vSpeed = vSpeed;
    }
}
