package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

public class Sound implements IResource {
    public String fileName = "";
    public String name;

    public Sound(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JSONValue toJSON() {
        JSONObject sound = new JSONObject();
        sound.put("name", new JSONValue(name));
        sound.put("file", new JSONValue(fileName));

        return new JSONValue(sound);
    }
}
