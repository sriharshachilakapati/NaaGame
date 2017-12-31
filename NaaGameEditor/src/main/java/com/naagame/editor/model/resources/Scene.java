package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scene implements IResource {
    public final List<Instance> instances = new ArrayList<>();
    public String name;

    public Scene(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JSONValue toJSON() {
        JSONArray instances = this.instances.stream().map(Instance::toJSON)
                .collect(Collectors.toCollection(JSONArray::new));

        JSONObject scene = new JSONObject();
        scene.put("name", new JSONValue(name));
        scene.put("instances", new JSONValue(instances));

        return new JSONValue(scene);
    }

    public static class Instance {
        public Entity entity;
        public float posX;
        public float posY;

        private JSONValue toJSON() {
            JSONObject instance = new JSONObject();

            instance.put("entity", new JSONValue(entity.name));
            instance.put("posX", new JSONValue(posX));
            instance.put("posY", new JSONValue(posY));

            return new JSONValue(instance);
        }
    }
}
