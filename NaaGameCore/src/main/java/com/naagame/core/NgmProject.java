package com.naagame.core;

import com.naagame.core.resources.*;

import java.util.ArrayList;
import java.util.List;

public final class NgmProject {

    public static final List<NgmTexture> textures = new ArrayList<>();
    public static final List<NgmSprite> sprites = new ArrayList<>();
    public static final List<NgmBackground> backgrounds = new ArrayList<>();
    public static final List<NgmSound> sounds = new ArrayList<>();
    public static final List<NgmEntity> entities = new ArrayList<>();
    public static final List<NgmScene> scenes = new ArrayList<>();

    public static <T extends IResource> T find(List<T> resourceList, String name) {
        return resourceList.stream().filter(r -> name.equals(r.getName())).findFirst().orElse(null);
    }

    private NgmProject() {
    }
}
