package com.naagame.editor.model;

import com.naagame.editor.model.resources.*;

import java.util.ArrayList;
import java.util.List;

public final class Resources {

    public static final List<Texture> textures = new ArrayList<>();
    public static final List<Sprite> sprites = new ArrayList<>();
    public static final List<Background> backgrounds = new ArrayList<>();
    public static final List<Sound> sounds = new ArrayList<>();
    public static final List<Entity> entities = new ArrayList<>();
    public static final List<Scene> scenes = new ArrayList<>();

    public static <T extends IResource> T find(List<T> resourceList, String name) {
        return resourceList.stream().filter(r -> name.equals(r.getName())).findFirst().orElse(null);
    }

    private Resources() {
    }
}
