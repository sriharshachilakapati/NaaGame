package com.naagame.editor.model.resources;

import java.util.ArrayList;
import java.util.List;

public class Scene implements IResource {
    private final List<Instance<Entity>> entities = new ArrayList<>();
    private final List<Instance<Background>> backgrounds = new ArrayList<>();

    private String name;

    public Scene(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Instance<Entity>> getEntities() {
        return entities;
    }

    public List<Instance<Background>> getBackgrounds() {
        return backgrounds;
    }

    public static class Instance<E extends IResource> {
        private E object;

        private float posX;
        private float posY;

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
