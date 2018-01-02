package com.naagame.editor.io;

import com.naagame.editor.model.Resources;
import com.naagame.editor.model.resources.*;
import com.shc.easyjson.JSON;
import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ProjectWriter {
    private ProjectWriter() {
    }

    private static <T> JSONValue listToJSON(List<T> list, Function<T, JSONValue> func) {
        return new JSONValue(list.stream().map(func).collect(Collectors.toCollection(JSONArray::new)));
    }

    private static JSONValue textureToJSON(Texture texture) {
        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(texture.getName()));
        json.put("file", new JSONValue(texture.getFileName()));

        return new JSONValue(json);
    }

    private static JSONValue spriteToJSON(Sprite sprite) {
        final Function<Sprite.Frame, JSONValue> frameToJSON = frame -> {
            JSONObject json = new JSONObject();

            json.put("texture", new JSONValue(frame.getTexture() == null ? "" : frame.getTexture().getName()));
            json.put("duration", new JSONValue(frame.getDuration()));

            return new JSONValue(json);
        };

        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(sprite.getName()));
        json.put("frames", listToJSON(sprite.getFrames(), frameToJSON));

        return new JSONValue(json);
    }

    private static JSONValue backgroundToJSON(Background background) {
        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(background.getName()));
        json.put("texture", new JSONValue(background.getTexture() == null ? "" : background.getTexture().getName()));
        json.put("hSpeed", new JSONValue(background.getHSpeed()));
        json.put("vSpeed", new JSONValue(background.getVSpeed()));

        return new JSONValue(json);
    }

    private static JSONValue soundToJSON(Sound sound) {
        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(sound.getName()));
        json.put("file", new JSONValue(sound.getFileName()));

        return new JSONValue(json);
    }

    private static JSONValue entityToJSON(Entity entity) {
        final Function<Entity.Event.Action, JSONValue> actionToJSON = action -> {
            JSONObject json = new JSONObject();

            json.put("code", new JSONValue(action.getCode()));
            json.put("args", new JSONValue(action.getArgs()));

            return new JSONValue(json);
        };

        final Function<Entity.Event, JSONValue> eventToJSON = event -> {
            JSONObject json = new JSONObject();

            json.put("type", new JSONValue(event.getType().toString()));
            json.put("actions", listToJSON(event.getActions(), actionToJSON));
            json.put("args", new JSONValue(event.getArgs()));

            return new JSONValue(json);
        };

        JSONObject json = new JSONObject();

        json.put("sprite", new JSONValue(entity.getSprite() == null ? "" : entity.getSprite().getName()));
        json.put("events", listToJSON(entity.getEvents(), eventToJSON));

        return new JSONValue(json);
    }

    private static JSONValue sceneToJSON(Scene scene) {
        final BiFunction<Scene.Instance<? extends IResource>, String, JSONValue> instanceToJSON = (instance, type) -> {
            JSONObject json = new JSONObject();

            json.put(type, new JSONValue(instance.getObject().getName()));
            json.put("posX", new JSONValue(instance.getPosX()));
            json.put("posY", new JSONValue(instance.getPosY()));

            return new JSONValue(json);
        };

        final Function<Scene.Instance<Entity>, JSONValue> entitiesToJSON = instance ->
                instanceToJSON.apply(instance, "entity");
        final Function<Scene.Instance<Background>, JSONValue> backgroundsToJSON = instance ->
                instanceToJSON.apply(instance, "background");

        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(scene.getName()));
        json.put("entities", listToJSON(scene.getEntities(), entitiesToJSON));
        json.put("backgrounds", listToJSON(scene.getBackgrounds(), backgroundsToJSON));

        return new JSONValue(json);
    }

    private static String jsonifyProject() {
        JSONObject json = new JSONObject();

        json.put("textures", listToJSON(Resources.textures, ProjectWriter::textureToJSON));
        json.put("sprites", listToJSON(Resources.sprites, ProjectWriter::spriteToJSON));
        json.put("backgrounds", listToJSON(Resources.backgrounds, ProjectWriter::backgroundToJSON));
        json.put("sounds", listToJSON(Resources.sounds, ProjectWriter::soundToJSON));
        json.put("entities", listToJSON(Resources.entities, ProjectWriter::entityToJSON));
        json.put("scenes", listToJSON(Resources.scenes, ProjectWriter::sceneToJSON));

        return JSON.write(json);
    }

    public static void writeToFile(Path path) throws IOException {
        Files.write(path, jsonifyProject().getBytes());
    }
}
