package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sprite implements IResource {
    public final List<Frame> frames = new ArrayList<>();
    public String name;

    public Sprite(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JSONValue toJSON() {
        JSONArray frames = new JSONArray();
        this.frames.stream().map(Frame::toJSON).collect(Collectors.toCollection(() -> frames));

        JSONObject sprite = new JSONObject();
        sprite.put("name", new JSONValue(name));
        sprite.put("frames", new JSONValue(frames));

        return new JSONValue(sprite);
    }

    public static class Frame {
        public Texture texture;
        public int duration;

        private JSONValue toJSON() {
            JSONObject frame = new JSONObject();

            frame.put("texture", new JSONValue(texture.name));
            frame.put("duration", new JSONValue(duration));

            return new JSONValue(frame);
        }
    }
}
