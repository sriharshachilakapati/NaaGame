package com.naagame.editor.model;

import com.naagame.editor.model.resources.*;
import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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

        JSONArray textures = new JSONArray();
        JSONArray sprites = new JSONArray();
        JSONArray backgrounds = new JSONArray();
        JSONArray sounds = new JSONArray();
        JSONArray entities = new JSONArray();
        JSONArray scenes = new JSONArray();

        final BiConsumer<List<? extends IResource>, JSONArray> allToJSON = (list, array) ->
                list.stream().map(IResource::toJSON).collect(Collectors.toCollection(() -> array));

        allToJSON.accept(Resources.textures, textures);
        allToJSON.accept(Resources.sprites, sprites);
        allToJSON.accept(Resources.backgrounds, backgrounds);
        allToJSON.accept(Resources.sounds, sounds);
        allToJSON.accept(Resources.entities, entities);
        allToJSON.accept(Resources.scenes, scenes);

        json.put("textures", new JSONValue(textures));
        json.put("sprites", new JSONValue(sprites));
        json.put("backgrounds", new JSONValue(backgrounds));
        json.put("sounds", new JSONValue(sounds));
        json.put("entities", new JSONValue(entities));
        json.put("scenes", new JSONValue(scenes));

        return json;
    }
}
