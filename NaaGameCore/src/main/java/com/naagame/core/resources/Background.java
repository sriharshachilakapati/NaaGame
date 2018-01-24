package com.naagame.core.resources;

public class Background implements IResource {
    private Texture texture;

    private float hSpeed;
    private float vSpeed;
    private String name;

    public Background(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getHSpeed() {
        return hSpeed;
    }

    public float getVSpeed() {
        return vSpeed;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setHSpeed(float hSpeed) {
        this.hSpeed = hSpeed;
    }

    public void setVSpeed(float vSpeed) {
        this.vSpeed = vSpeed;
    }
}
