package com.naagame.core;

import com.naagame.core.resources.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void pruneDeadReferences() {
        // Remove all frames in sprites whose texture is deleted
        sprites.forEach(sprite -> sprite.getFrames().removeAll(sprite.getFrames()
                .stream().filter(frame -> !textures.contains(frame.getTexture()))
                .collect(Collectors.toList())));

        // Reset all texture fields in background to null if texture is deleted
        for (NgmBackground background : backgrounds) {
            if (background.getTexture() != null) {
                background.setTexture(find(textures, background.getTexture().getName()));
            }
        }

        // Reset the sprite field of entity to null if sprite is deleted
        for (NgmEntity entity : entities) {
            if (entity.getSprite() != null) {
                entity.setSprite(find(sprites, entity.getSprite().getName()));
            }
        }

        // Remove all instances in scene that points to deleted entities / backgrounds
        scenes.forEach(scene -> {
            scene.getEntities().removeAll(scene.getEntities().stream()
                    .filter(instance -> !entities.contains(instance.getObject()))
                    .collect(Collectors.toList()));

            scene.getBackgrounds().removeAll(scene.getBackgrounds().stream()
                    .filter(instance -> !backgrounds.contains(instance.getObject()))
                    .collect(Collectors.toList()));
        });
    }
}
