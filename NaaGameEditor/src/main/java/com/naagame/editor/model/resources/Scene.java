package com.naagame.editor.model.resources;

import java.util.ArrayList;
import java.util.List;

public class Scene implements IResource {
    private final List<Instance> instances = new ArrayList<>();
    private String name;

    public Scene(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public static class Instance {
        private Entity entity;
        private float posX;
        private float posY;

        public Entity getEntity() {
            return entity;
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;
        }
    }
}
