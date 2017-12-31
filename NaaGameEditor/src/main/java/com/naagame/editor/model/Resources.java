package com.naagame.editor.model;

import com.naagame.editor.model.resources.*;
import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Resources {

    public static final List<Texture> textures = new ArrayList<>();
    public static final List<Sprite> sprites = new ArrayList<>();
    public static final List<Background> backgrounds = new ArrayList<>();
    public static final List<Sound> sounds = new ArrayList<>();
    public static final List<Entity> entities = new ArrayList<>();
    public static final List<Scene> scenes = new ArrayList<>();

    private Resources() {
    }

    public static JSONObject exportToJSON() {
        JSONObject json = new JSONObject();

        final Function<List<? extends IResource>, JSONArray> allToJSON = list ->
                list.stream().map(IResource::toJSON).collect(Collectors.toCollection(JSONArray::new));

        JSONArray textures = allToJSON.apply(Resources.textures);
        JSONArray sprites = allToJSON.apply(Resources.sprites);
        JSONArray backgrounds = allToJSON.apply(Resources.backgrounds);
        JSONArray sounds = allToJSON.apply(Resources.sounds);
        JSONArray entities = allToJSON.apply(Resources.entities);
        JSONArray scenes = allToJSON.apply(Resources.scenes);

        json.put("textures", new JSONValue(textures));
        json.put("sprites", new JSONValue(sprites));
        json.put("backgrounds", new JSONValue(backgrounds));
        json.put("sounds", new JSONValue(sounds));
        json.put("entities", new JSONValue(entities));
        json.put("scenes", new JSONValue(scenes));

        return json;
    }
}
