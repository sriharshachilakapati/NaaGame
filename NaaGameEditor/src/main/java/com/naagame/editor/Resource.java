package com.naagame.editor;

import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class Resource {
    public static final List<Texture> textures = new ArrayList<>();
    public static final List<Sprite> sprites = new ArrayList<>();
    public static final List<Background> backgrounds = new ArrayList<>();
    public static final List<Sound> sounds = new ArrayList<>();
    public static final List<Entity> entities = new ArrayList<>();
    public static final List<Scene> scenes = new ArrayList<>();

    public enum Type {
        TEXTURE,
        SPRITE,
        BACKGROUND,
        SOUND,
        ENTITY,
        SCENE
    }

    public final Type type;

    public String name;

    protected Resource(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public static JSONObject exportToJSON() {
        JSONObject json = new JSONObject();

        JSONArray textures = new JSONArray();
        JSONArray sprites = new JSONArray();
        JSONArray backgrounds = new JSONArray();
        JSONArray sounds = new JSONArray();
        JSONArray entities = new JSONArray();
        JSONArray scenes = new JSONArray();

        final BiConsumer<List<? extends Resource>, JSONArray> allToJSON = (list, array) ->
                list.stream().map(Resource::toJSON).collect(Collectors.toCollection(() -> array));

        allToJSON.accept(Resource.textures, textures);
        allToJSON.accept(Resource.sprites, sprites);
        allToJSON.accept(Resource.backgrounds, backgrounds);
        allToJSON.accept(Resource.sounds, sounds);
        allToJSON.accept(Resource.entities, entities);
        allToJSON.accept(Resource.scenes, scenes);

        json.put("textures", new JSONValue(textures));
        json.put("sprites", new JSONValue(sprites));
        json.put("backgrounds", new JSONValue(backgrounds));
        json.put("sounds", new JSONValue(sounds));
        json.put("entities", new JSONValue(entities));
        json.put("scenes", new JSONValue(scenes));

        return json;
    }

    public abstract JSONValue toJSON();

    public static class Texture extends Resource {
        public String fileName = "";

        public Texture(String name) {
            super(Type.TEXTURE, name);
        }

        @Override
        public JSONValue toJSON() {
            JSONObject texture = new JSONObject();

            texture.put("name", new JSONValue(name));
            texture.put("file", new JSONValue(fileName));

            return new JSONValue(texture);
        }
    }

    public static class Sprite extends Resource {
        public final List<Frame> frames = new ArrayList<>();

        public Sprite(String name) {
            super(Type.SPRITE, name);
        }

        @Override
        public JSONValue toJSON() {
            JSONArray frames = new JSONArray();
            this.frames.stream().map(Frame::toJSON).collect(Collectors.toCollection(() -> frames));

            JSONObject sprite = new JSONObject();
            sprite.put("name", new JSONValue(name));
            sprite.put("frames", new JSONValue(frames));

            return new JSONValue(sprite);
        }

        public static class Frame {
            public Texture texture;
            public int duration;

            private JSONValue toJSON() {
                JSONObject frame = new JSONObject();

                frame.put("texture", new JSONValue(texture.name));
                frame.put("duration", new JSONValue(duration));

                return new JSONValue(frame);
            }
        }
    }

    public static class Background extends Resource {
        public Texture texture;

        public float hSpeed;
        public float vSpeed;

        public Background(String name) {
            super(Type.BACKGROUND, name);
        }

        @Override
        public JSONValue toJSON() {
            JSONObject background = new JSONObject();

            background.put("name", new JSONValue(name));
            background.put("texture", new JSONValue(texture == null ? "" : texture.name));
            background.put("hSpeed", new JSONValue(hSpeed));
            background.put("vSpeed", new JSONValue(vSpeed));

            return new JSONValue(background);
        }
    }

    public static class Sound extends Resource {
        public String fileName = "";

        public Sound(String name) {
            super(Type.SOUND, name);
        }

        @Override
        public JSONValue toJSON() {
            JSONObject sound = new JSONObject();
            sound.put("name", new JSONValue(name));
            sound.put("file", new JSONValue(fileName));

            return new JSONValue(sound);
        }
    }

    public static class Entity extends Resource {
        public final List<Event> events = new ArrayList<>();

        public Sprite sprite;

        public Entity(String name) {
            super(Type.ENTITY, name);
        }

        @Override
        public JSONValue toJSON() {
            JSONArray events = new JSONArray();
            this.events.stream().map(Event::toJSON).collect(Collectors.toCollection(() -> events));

            JSONObject entity = new JSONObject();
            entity.put("sprite", new JSONValue(sprite == null ? "" : sprite.name));
            entity.put("events", new JSONValue(events));

            return new JSONValue(entity);
        }

        public static class Event {
            public enum Type {
                CREATE,
                UPDATE,
                COLLISION
            }

            public final Type type;
            public final List<Action> actions = new ArrayList<>();

            public String eventArgs;

            public Event(Type type) {
                this.type = type;
            }

            private JSONValue toJSON() {
                JSONArray actions = new JSONArray();
                this.actions.stream().map(Action::toJSON).collect(Collectors.toCollection(() -> actions));

                JSONObject event = new JSONObject();
                event.put("type", new JSONValue(type.toString()));
                event.put("actions", new JSONValue(actions));

                return new JSONValue(event);
            }

            public final class Action {
                public final int actionCode;
                public String actionArgs;

                public Action(int actionCode, String actionArgs) {
                    this.actionCode = actionCode;
                    this.actionArgs = actionArgs;
                }

                private JSONValue toJSON() {
                    JSONObject action = new JSONObject();

                    action.put("code", new JSONValue(actionCode));
                    action.put("args", new JSONValue(actionArgs));

                    return new JSONValue(action);
                }
            }
        }
    }

    public static class Scene extends Resource {
        public final List<Instance> instances = new ArrayList<>();

        public Scene(String name) {
            super(Type.SCENE, name);
        }

        @Override
        public JSONValue toJSON() {
            JSONArray instances = new JSONArray();
            this.instances.stream().map(Instance::toJSON).collect(Collectors.toCollection(() -> instances));

            JSONObject scene = new JSONObject();
            scene.put("name", new JSONValue(name));
            scene.put("instances", new JSONValue(instances));

            return new JSONValue(scene);
        }

        public static class Instance {
            public Entity entity;
            public float posX;
            public float posY;

            private JSONValue toJSON() {
                JSONObject instance = new JSONObject();

                instance.put("entity", new JSONValue(entity.name));
                instance.put("posX", new JSONValue(posX));
                instance.put("posY", new JSONValue(posY));

                return new JSONValue(instance);
            }
        }
    }
}
