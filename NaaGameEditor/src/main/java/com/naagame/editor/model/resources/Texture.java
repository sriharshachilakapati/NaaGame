package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

public class Texture implements IResource {
    public String fileName = "";
    public String name;

    public Texture(String name) {
        this.name = name;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JSONValue toJSON() {
        JSONObject texture = new JSONObject();

        texture.put("name", new JSONValue(name));
        texture.put("file", new JSONValue(fileName));

        return new JSONValue(texture);
    }
}
