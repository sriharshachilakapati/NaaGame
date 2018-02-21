package com.naagame.editor.io;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.*;
import com.shc.easyjson.JSON;
import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static JSONValue textureToJSON(NgmTexture texture) {
        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(texture.getName()));
        json.put("source", new JSONValue(texture.getSource()));

        return new JSONValue(json);
    }

    private static JSONValue spriteToJSON(NgmSprite sprite) {
        final Function<NgmSprite.Frame, JSONValue> frameToJSON = frame -> {
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

    private static JSONValue backgroundToJSON(NgmBackground background) {
        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(background.getName()));
        json.put("texture", new JSONValue(background.getTexture() == null ? "" : background.getTexture().getName()));
        json.put("hSpeed", new JSONValue(background.getHSpeed()));
        json.put("vSpeed", new JSONValue(background.getVSpeed()));

        return new JSONValue(json);
    }

    private static JSONValue soundToJSON(NgmSound sound) {
        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(sound.getName()));
        json.put("source", new JSONValue(sound.getSource()));

        return new JSONValue(json);
    }

    private static JSONValue entityToJSON(NgmEntity entity) {
        final Function<NgmEntity.Event.Action, JSONValue> actionToJSON = action -> {
            JSONObject json = new JSONObject();

            json.put("code", new JSONValue(action.getCode()));
            json.put("args", new JSONValue(action.getArgs()));

            return new JSONValue(json);
        };

        final Function<NgmEntity.Event, JSONValue> eventToJSON = event -> {
            JSONObject json = new JSONObject();

            json.put("type", new JSONValue(event.getType().toString().toLowerCase()));
            json.put("actions", listToJSON(event.getActions(), actionToJSON));
            json.put("args", new JSONValue(event.getArgs()));

            return new JSONValue(json);
        };

        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(entity.getName()));
        json.put("sprite", new JSONValue(entity.getSprite() == null ? "" : entity.getSprite().getName()));
        json.put("events", listToJSON(entity.getEvents(), eventToJSON));

        return new JSONValue(json);
    }

    private static JSONValue sceneToJSON(NgmScene scene) {
        final BiFunction<NgmScene.Instance<? extends IResource>, String, JSONValue> instanceToJSON = (instance, type) -> {
            JSONObject json = new JSONObject();

            json.put(type, new JSONValue(instance.getObject().getName()));
            json.put("posX", new JSONValue(instance.getPosX()));
            json.put("posY", new JSONValue(instance.getPosY()));

            return new JSONValue(json);
        };

        final Function<NgmScene.Instance<NgmEntity>, JSONValue> entitiesToJSON = instance ->
                instanceToJSON.apply(instance, "entity");
        final Function<NgmScene.Instance<NgmBackground>, JSONValue> backgroundsToJSON = instance ->
                instanceToJSON.apply(instance, "background");

        JSONObject json = new JSONObject();

        json.put("name", new JSONValue(scene.getName()));
        json.put("entities", listToJSON(scene.getEntities(), entitiesToJSON));
        json.put("backgrounds", listToJSON(scene.getBackgrounds(), backgroundsToJSON));

        return new JSONValue(json);
    }

    private static String jsonifyProject() {
        JSONObject json = new JSONObject();

        json.put("textures", listToJSON(NgmProject.textures, ProjectWriter::textureToJSON));
        json.put("sprites", listToJSON(NgmProject.sprites, ProjectWriter::spriteToJSON));
        json.put("backgrounds", listToJSON(NgmProject.backgrounds, ProjectWriter::backgroundToJSON));
        json.put("sounds", listToJSON(NgmProject.sounds, ProjectWriter::soundToJSON));
        json.put("entities", listToJSON(NgmProject.entities, ProjectWriter::entityToJSON));
        json.put("scenes", listToJSON(NgmProject.scenes, ProjectWriter::sceneToJSON));

        return JSON.write(json);
    }

    public static void writeToFile(Path path) throws IOException {
        Files.write(path, jsonifyProject().getBytes());

        if (!Files.exists(path.getParent().resolve("resources"))) {
            Files.createDirectory(path.getParent().resolve("resources"));
        }

        for (NgmTexture texture : NgmProject.textures) {
            if (!texture.getSource().startsWith("colour:") && !"".equals(texture.getSource().trim())) {
                System.out.println(ProjectResourceWriter.writeFile(Paths.get(texture.getSource()), path.getParent()));
            }
        }

        for (NgmSound sound : NgmProject.sounds) {
            if (!"".equals(sound.getSource().trim())) {
                System.out.println(ProjectResourceWriter.writeFile(Paths.get(sound.getSource()), path.getParent()));
            }
        }
    }
}
