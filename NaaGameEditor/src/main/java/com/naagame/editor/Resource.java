package com.naagame.editor;

import java.util.ArrayList;
import java.util.List;

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

    public static class Texture extends Resource {
        public String fileName = null;

        public Texture(String name) {
            super(Type.TEXTURE, name);
        }
    }

    public static class Sprite extends Resource {
        public final List<Frame> frames = new ArrayList<>();

        public Sprite(String name) {
            super(Type.SPRITE, name);
        }

        public static class Frame {
            public Texture texture;
            public int duration;
        }
    }

    public static class Background extends Resource {
        public Texture texture;
        public float hSpeed;
        public float vSpeed;

        public Background(String name) {
            super(Type.BACKGROUND, name);
        }
    }

    public static class Sound extends Resource {
        public String fileName = null;

        public Sound(String name) {
            super(Type.SOUND, name);
        }
    }

    public static class Entity extends Resource {
        public final List<Event> events = new ArrayList<>();

        public Sprite sprite;

        public Entity(String name) {
            super(Type.ENTITY, name);
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

            public final class Action {
                public final int actionCode;
                public String actionArgs;

                public Action(int actionCode, String actionArgs) {
                    this.actionCode = actionCode;
                    this.actionArgs = actionArgs;
                }
            }
        }
    }

    public static class Scene extends Resource {
        public final List<Instance> instances = new ArrayList<>();

        public Scene(String name) {
            super(Type.SCENE, name);
        }

        public static class Instance {
            public Entity entity;
            public float posX;
            public float posY;
        }
    }
}
