package com.naagame.core.resources;

import java.util.ArrayList;
import java.util.List;

public class NgmScene implements IResource {
    private final List<Instance<NgmEntity>> entities = new ArrayList<>();
    private final List<Instance<NgmBackground>> backgrounds = new ArrayList<>();

    private String name;

    public NgmScene(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Instance<NgmEntity>> getEntities() {
        return entities;
    }

    public List<Instance<NgmBackground>> getBackgrounds() {
        return backgrounds;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public static class Instance<E extends IResource> {
        private E object;

        private float posX;
        private float posY;

        public Instance() {}

        public Instance(E object, float posX, float posY) {
            this.object = object;
            this.posX = posX;
            this.posY = posY;
        }

        public E getObject() {
            return object;
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;
        }

        public void setObject(E object) {
            this.object = object;
        }

        public void setPosX(float posX) {
            this.posX = posX;
        }

        public void setPosY(float posY) {
            this.posY = posY;
        }
    }
}
