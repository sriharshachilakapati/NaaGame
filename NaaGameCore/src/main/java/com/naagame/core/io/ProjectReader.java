package com.naagame.core.io;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.*;
import com.shc.easyjson.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ProjectReader {
    private ProjectReader() {
    }

    private static <T> void loadToList(List<T> list, JSONValue array, Function<JSONObject, T> function) {
        list.clear();
        final Function<JSONValue, T> mapper = function.compose(JSONValue::<JSONObject> getValue);
        array.<JSONArray> getValue().stream().map(mapper).collect(Collectors.toCollection(() -> list));
    }

    private static NgmTexture jsonToTexture(JSONObject json) {
        NgmTexture texture = new NgmTexture(json.get("name").getValue());
        texture.setSource(json.get("source").getValue());
        return texture;
    }

    private static NgmSprite jsonToSprite(JSONObject json) {
        final Function<JSONValue, NgmSprite.Frame> jsonToFrame = value -> {
            JSONObject frame = value.getValue();

            final String texture = frame.get("texture").getValue();
            final int duration = frame.get("duration").<Number> getValue().intValue();

            return new NgmSprite.Frame(NgmProject.find(NgmProject.textures, texture), duration);
        };

        NgmSprite sprite = new NgmSprite(json.get("name").getValue());
        JSONArray frames = json.get("frames").getValue();
        sprite.setFrames(frames.stream().map(jsonToFrame).collect(Collectors.toList()));

        return sprite;
    }

    private static NgmBackground jsonToBackground(JSONObject json) {
        NgmBackground background = new NgmBackground(json.get("name").getValue());

        background.setTexture(NgmProject.find(NgmProject.textures, json.get("texture").getValue()));
        background.setHSpeed(json.get("hSpeed").<Number> getValue().floatValue());
        background.setVSpeed(json.get("vSpeed").<Number> getValue().floatValue());

        return background;
    }

    private static NgmSound jsonToSound(JSONObject json) {
        NgmSound sound = new NgmSound(json.get("name").getValue());
        sound.setSource(json.get("source").getValue());
        return sound;
    }

    private static NgmEntity jsonToEntity(JSONObject json) {
        final Function<JSONObject, NgmEntity.Event.Action> jsonToAction = action -> {
            final String code = action.get("code").getValue();
            final String args = action.get("args").getValue();

            return new NgmEntity.Event.Action(code, args);
        };

        final Function<JSONObject, NgmEntity.Event> jsonToEvent = eventJSON -> {
            final NgmEntity.Event.Type type = NgmEntity.Event.Type.valueOf(eventJSON.get("type").<String> getValue().toUpperCase());
            final String args = eventJSON.get("args").getValue();

            NgmEntity.Event event = new NgmEntity.Event(type, args);
            loadToList(event.getActions(), eventJSON.get("actions"), jsonToAction);

            return event;
        };

        NgmEntity entity = new NgmEntity(json.get("name").getValue());
        entity.setSprite(NgmProject.find(NgmProject.sprites, json.get("sprite").getValue()));
        loadToList(entity.getEvents(), json.get("events"), jsonToEvent);

        return entity;
    }

    private static <T extends IResource> NgmScene.Instance<T> jsonToInstance(JSONObject json,
                                                                             Function<String, T> finder, String type) {
        NgmScene.Instance<T> instance = new NgmScene.Instance<>();

        instance.setObject(finder.apply(json.get(type).getValue()));
        instance.setPosX(json.get("posX").<Number> getValue().floatValue());
        instance.setPosY(json.get("posY").<Number> getValue().floatValue());

        return instance;
    }

    private static NgmScene jsonToScene(JSONObject json) {
        final Function<JSONObject, NgmScene.Instance<NgmEntity>> jsonToEntityInstance = entityJSON ->
                jsonToInstance(entityJSON, name -> NgmProject.find(NgmProject.entities, name), "entity");

        final Function<JSONObject, NgmScene.Instance<NgmBackground>> jsonToBackgroundInstance = backgroundJSON ->
                jsonToInstance(backgroundJSON, name -> NgmProject.find(NgmProject.backgrounds, name), "background");

        NgmScene scene = new NgmScene(json.get("name").getValue());
        loadToList(scene.getEntities(), json.get("entities"), jsonToEntityInstance);
        loadToList(scene.getBackgrounds(), json.get("backgrounds"), jsonToBackgroundInstance);

        return scene;
    }

    private static void loadProjectFromJSON(JSONObject json) {
        loadToList(NgmProject.textures, json.get("textures"), ProjectReader::jsonToTexture);
        loadToList(NgmProject.sprites, json.get("sprites"), ProjectReader::jsonToSprite);
        loadToList(NgmProject.backgrounds, json.get("backgrounds"), ProjectReader::jsonToBackground);
        loadToList(NgmProject.sounds, json.get("sounds"), ProjectReader::jsonToSound);
        loadToList(NgmProject.entities, json.get("entities"), ProjectReader::jsonToEntity);
        loadToList(NgmProject.scenes, json.get("scenes"), ProjectReader::jsonToScene);
    }

    public static void loadFromJSON(String json) throws ParseException {
        JSONObject jsonObject = JSON.parse(json);
        loadProjectFromJSON(jsonObject);
    }
}
