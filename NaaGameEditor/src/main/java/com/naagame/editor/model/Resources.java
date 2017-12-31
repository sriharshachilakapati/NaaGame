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

    private Resources() {
    }
}
