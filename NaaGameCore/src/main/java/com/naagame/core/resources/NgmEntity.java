package com.naagame.core.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NgmEntity implements IResource {
    private final List<Event> events = new ArrayList<>();

    private NgmSprite sprite;
    private String name;

    public NgmEntity(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public NgmSprite getSprite() {
        return sprite;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setSprite(NgmSprite sprite) {
        this.sprite = sprite;
    }

    public static class Event {
        private final Event.Type type;
        private final List<Event.Action> actions = new ArrayList<>();

        private String args;

        public Event(Event.Type type, String args) {
            this.type = type;
            this.args = args;
        }

        public Type getType() {
            return type;
        }

        public List<Action> getActions() {
            return actions;
        }

        public String getArgs() {
            return args;
        }

        public enum Type {
            CREATE,
            UPDATE,
            KEY_DOWN,
            KEY_UP,
            KEY_TAP,
            MOUSE_DOWN,
            MOUSE_UP,
            MOUSE_TAP,
            OUT_OF_BOUNDS,
            COLLISION,
            DESTROY
        }

        public static class Action implements Serializable {
            private final String code;
            private String args;

            public Action(String code, String args) {
                this.code = code;
                this.args = args;
            }

            public String getCode() {
                return code;
            }

            public String getArgs() {
                return args;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Action action = (Action) o;
                return Objects.equals(code, action.code) &&
                        Objects.equals(args, action.args);
            }

            @Override
            public int hashCode() {
                return Objects.hash(code, args);
            }
        }
    }
}
