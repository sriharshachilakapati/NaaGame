package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

public class Background implements IResource {
    public Texture texture;

    public float hSpeed;
    public float vSpeed;
    public String name;

    public Background(String name) {
        this.name = name;
    }

    @Override
    public JSONValue toJSON() {
        JSONObject background = new JSONObject();

        background.put("name", new JSONValue(name));
        background.put("texture", new JSONValue(texture == null ? "" : texture.name));
        background.put("hSpeed", new JSONValue(hSpeed));
        background.put("vSpeed", new JSONValue(vSpeed));

        return new JSONValue(background);
    }

    @Override
    public String getName() {
        return name;
    }
}
