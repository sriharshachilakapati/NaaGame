package com.naagame.editor.io;

import com.naagame.editor.model.Resources;
import com.naagame.editor.model.resources.*;
import com.shc.easyjson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static Texture jsonToTexture(JSONObject json) {
        Texture texture = new Texture(json.get("name").getValue());
        texture.setFileName(json.get("file").getValue());
        return texture;
    }

    private static Sprite jsonToSprite(JSONObject json) {
        final Function<JSONValue, Sprite.Frame> jsonToFrame = value -> {
            JSONObject frame = value.getValue();

            final String texture = frame.get("texture").getValue();
            final int duration = frame.get("duration").<Number> getValue().intValue();

            return new Sprite.Frame(Resources.find(Resources.textures, texture), duration);
        };

        Sprite sprite = new Sprite(json.get("name").getValue());
        JSONArray frames = json.get("frames").getValue();
        sprite.setFrames(frames.stream().map(jsonToFrame).collect(Collectors.toList()));

        return sprite;
    }

    private static Background jsonToBackground(JSONObject json) {
        Background background = new Background(json.get("name").getValue());

        background.setTexture(Resources.find(Resources.textures, json.get("texture").getValue()));
        background.setHSpeed(json.get("hSpeed").<Number> getValue().floatValue());
        background.setVSpeed(json.get("vSpeed").<Number> getValue().floatValue());

        return background;
    }

    private static Sound jsonToSound(JSONObject json) {
        Sound sound = new Sound(json.get("name").getValue());
        sound.setFileName(json.get("file").getValue());
        return sound;
    }

    private static Entity jsonToEntity(JSONObject json) {
        final Function<JSONObject, Entity.Event.Action> jsonToAction = action -> {
            final int code = action.get("code").<Number> getValue().intValue();
            final String args = action.get("args").getValue();

            return new Entity.Event.Action(code, args);
        };

        final Function<JSONObject, Entity.Event> jsonToEvent = eventJSON -> {
            final Entity.Event.Type type = Entity.Event.Type.valueOf(eventJSON.get("type").getValue());
            final String args = eventJSON.get("args").getValue();

            Entity.Event event = new Entity.Event(type, args);
            loadToList(event.getActions(), eventJSON.get("actions"), jsonToAction);

            return event;
        };

        Entity entity = new Entity(json.get("name").getValue());
        entity.setSprite(Resources.find(Resources.sprites, json.get("sprite").getValue()));
        loadToList(entity.getEvents(), json.get("events"), jsonToEvent);

        return entity;
    }

    private static Scene jsonToScene(JSONObject json) {
        return null;
    }

    private static void loadProjectFromJSON(JSONObject json) {
        loadToList(Resources.textures, json.get("textures"), ProjectReader::jsonToTexture);
        loadToList(Resources.sprites, json.get("sprites"), ProjectReader::jsonToSprite);
        loadToList(Resources.backgrounds, json.get("backgrounds"), ProjectReader::jsonToBackground);
        loadToList(Resources.sounds, json.get("sounds"), ProjectReader::jsonToSound);
        loadToList(Resources.entities, json.get("entities"), ProjectReader::jsonToEntity);
        loadToList(Resources.scenes, json.get("scenes"), ProjectReader::jsonToScene);
    }

    public static void loadFromFile(Path path) throws IOException, ParseException {
        String json = new String(Files.readAllBytes(path));
        JSONObject jsonObject = JSON.parse(json);
        loadProjectFromJSON(jsonObject);
    }
}
