package com.naagame.editor.util;

import com.naagame.core.NgmProject;
import com.naagame.core.action.ActionArgument;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.action.control.LibControl;
import com.naagame.core.action.debug.LibDebug;
import com.naagame.core.action.movement.LibMovement;
import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectUtils {
    public static ActionDefinition<?> findDefinition(String code) {
        final Function<Field[], List<ActionDefinition<?>>> getDefinitions = fields -> Arrays.stream(fields).map(f -> {
            try {
                return (ActionDefinition<?>) f.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return null;
        }).collect(Collectors.toList());

        List<ActionDefinition<?>> actionDefinitions = new ArrayList<>();

        actionDefinitions.addAll(getDefinitions.apply(LibDebug.class.getDeclaredFields()));
        actionDefinitions.addAll(getDefinitions.apply(LibMovement.class.getDeclaredFields()));
        actionDefinitions.addAll(getDefinitions.apply(LibControl.class.getDeclaredFields()));

        return actionDefinitions.stream().filter(ad -> ad.getCode().equals(code)).findFirst().orElse(null);
    }

    public static <T> boolean hasDeadReference(ActionDefinition<T> definition, NgmEntity.Event.Action action) {
        T object = definition.decode(action.getArgs(), definition.getSupplier().get());

        for (ActionArgument<T> argument : definition.getArguments()) {
            ArgumentType<?> type = argument.getType();

            if (type == ArgumentType.ENTITY || type == ArgumentType.SCENE || type == ArgumentType.SOUND) {
                if (argument.getGetter().apply(object) == null) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void pruneDeadReferences() {
        // Remove all frames in sprites whose texture is deleted
        NgmProject.sprites.forEach(sprite -> sprite.getFrames().removeAll(sprite.getFrames()
                .stream().filter(frame -> !NgmProject.textures.contains(frame.getTexture()))
                .collect(Collectors.toList())));

        // Reset all texture fields in background to null if texture is deleted
        for (NgmBackground background : NgmProject.backgrounds) {
            if (background.getTexture() != null) {
                background.setTexture(NgmProject.find(NgmProject.textures, background.getTexture().getName()));
            }
        }

        for (NgmEntity entity : NgmProject.entities) {
            // Reset the sprite field of entity to null if sprite is deleted
            if (entity.getSprite() != null) {
                entity.setSprite(NgmProject.find(NgmProject.sprites, entity.getSprite().getName()));
            }

            // Remove Events if their arguments are dead
            entity.getEvents().removeIf(
                    event -> (event.getType() == NgmEntity.Event.Type.COLLISION ||
                            event.getType() == NgmEntity.Event.Type.NONE_EXISTS) &&
                            NgmProject.find(NgmProject.entities, event.getArgs()) == null
            );

            // Delete all actions having dead argument parameters
            for (NgmEntity.Event event : entity.getEvents()) {
                event.getActions().removeIf(action -> {
                    ActionDefinition<?> actionDef = findDefinition(action.getCode());
                    return hasDeadReference(actionDef, action);
                });
            }
        }

        // Remove all instances in scene that points to deleted entities / backgrounds
        NgmProject.scenes.forEach(scene -> {
            scene.getEntities().removeAll(scene.getEntities().stream()
                    .filter(instance -> !NgmProject.entities.contains(instance.getObject()))
                    .collect(Collectors.toList()));

            scene.getBackgrounds().removeAll(scene.getBackgrounds().stream()
                    .filter(instance -> !NgmProject.backgrounds.contains(instance.getObject()))
                    .collect(Collectors.toList()));
        });
    }
}
