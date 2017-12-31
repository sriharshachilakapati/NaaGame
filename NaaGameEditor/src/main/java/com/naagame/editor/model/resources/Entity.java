package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONArray;
import com.shc.easyjson.JSONObject;
import com.shc.easyjson.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Entity implements IResource {
    public final List<Event> events = new ArrayList<>();

    public Sprite sprite;
    public String name;

    public Entity(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
        public final Event.Type type;
        public final List<Event.Action> actions = new ArrayList<>();
        public String eventArgs;

        public Event(Event.Type type) {
            this.type = type;
        }

        private JSONValue toJSON() {
            JSONArray actions = new JSONArray();
            this.actions.stream().map(Event.Action::toJSON).collect(Collectors.toCollection(() -> actions));

            JSONObject event = new JSONObject();
            event.put("type", new JSONValue(type.toString()));
            event.put("actions", new JSONValue(actions));

            return new JSONValue(event);
        }

        public enum Type {
            CREATE,
            UPDATE,
            COLLISION
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
