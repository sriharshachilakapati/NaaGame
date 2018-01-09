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
        return null;
    }

    private static Background jsonToBackground(JSONObject json) {
        return null;
    }

    private static Sound jsonToSound(JSONObject json) {
        return null;
    }

    private static Entity jsonToEntity(JSONObject json) {
        return null;
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
