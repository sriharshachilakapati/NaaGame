package com.naagame.editor.model.resources;

import java.util.ArrayList;
import java.util.List;

public class Entity implements IResource {
    private final List<Event> events = new ArrayList<>();

    private Sprite sprite;
    private String name;

    public Entity(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public Sprite getSprite() {
        return sprite;
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
            COLLISION
        }

        public final class Action {
            private final int code;
            private String args;

            public Action(int code, String args) {
                this.code = code;
                this.args = args;
            }

            public int getCode() {
                return code;
            }

            public String getArgs() {
                return args;
            }
        }
    }
}
