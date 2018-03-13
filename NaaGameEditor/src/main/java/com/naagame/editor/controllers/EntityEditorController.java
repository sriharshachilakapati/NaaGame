package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.control.LibControl;
import com.naagame.core.action.debug.LibDebug;
import com.naagame.core.action.movement.LibMovement;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import com.shc.easyjson.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityEditorController extends Controller implements Initializable {

    private static JSONObject keyCodes;
    private static Map<String, String> keyCodeNames;

    static {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(EntityEditorController.class
                .getClassLoader().getResourceAsStream("keyCodes.json")))) {
            String json = reader.lines().collect(Collectors.joining("\n"));
            keyCodes = JSON.parse(json);

            keyCodeNames = new HashMap<>();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML private ChoiceBox<String> spriteSelector;

    @FXML private Menu addEvtKeyPressedMenu;
    @FXML private Menu addEvtKeyReleasedMenu;
    @FXML private Menu addEvtKeyTappedMenu;

    @FXML private ListView<ActionDefinition<?>> debugActionsList;
    @FXML private ListView<ActionDefinition<?>> movementActionsList;
    @FXML private ListView<ActionDefinition<?>> controlActionsList;

    @FXML private ListView<NgmEntity.Event> eventsList;
    @FXML private ListView<NgmEntity.Event.Action> actionsList;

    private NgmEntity.Event currentEvent;

    private NgmEntity entity;
    private NgmSprite sprite;
    private boolean changed;

    @Override
    public void init(String name) {
        entity = NgmProject.find(NgmProject.entities, name);
        sprite = entity.getSprite();
        changed = false;

        if (sprite != null) {
            spriteSelector.getSelectionModel().select(sprite.getName());
        }

        eventsList.getItems().clear();
        eventsList.getItems().addAll(entity.getEvents().stream()
                .map(this::cloneEvent)
                .collect(Collectors.toList()));

        currentEvent = eventsList.getItems().stream().findFirst().orElse(null);

        if (currentEvent != null) {
            actionsList.getItems().clear();
            actionsList.getItems().addAll(currentEvent.getActions());
        }

        resourcesChanged();
    }

    private NgmEntity.Event.Action cloneAction(NgmEntity.Event.Action action) {
        return new NgmEntity.Event.Action(action.getCode(), action.getArgs());
    }

    private NgmEntity.Event cloneEvent(NgmEntity.Event event) {
        NgmEntity.Event newEvent = new NgmEntity.Event(event.getType(), event.getArgs());

        newEvent.getActions().addAll(event.getActions().stream()
                .map(this::cloneAction)
                .collect(Collectors.toList()));

        return newEvent;
    }

    @FXML
    @Override
    protected void discardChanges() {
        init(entity.getName());
    }

    @Override
    protected void resourcesChanged() {
        spriteSelector.getItems().clear();
        spriteSelector.getItems().addAll(NgmProject.sprites.stream()
                .map(NgmSprite::getName)
                .collect(Collectors.toList()));

        if (sprite == null) {
            return;
        }

        sprite = NgmProject.find(NgmProject.sprites, sprite.getName());

        if (sprite == null) {
            spriteSelector.getSelectionModel().clearSelection();
        } else {
            spriteSelector.getSelectionModel().select(sprite.getName());
        }
    }

    @Override
    protected boolean hasUnsavedEdits() {
        return changed;
    }

    @FXML
    @Override
    protected void commitChanges() {
        entity.setSprite(sprite);
        changed = false;
        notifySave();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spriteSelector.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (n != null) {
                sprite = NgmProject.find(NgmProject.sprites, n);
                changed = true;
            }
        });

        createKeyMenu(addEvtKeyPressedMenu, NgmEntity.Event.Type.KEY_DOWN);
        createKeyMenu(addEvtKeyReleasedMenu, NgmEntity.Event.Type.KEY_UP);
        createKeyMenu(addEvtKeyTappedMenu, NgmEntity.Event.Type.KEY_TAP);

        createActionLibraryItems(debugActionsList, LibDebug.class);
        createActionLibraryItems(movementActionsList, LibMovement.class);
        createActionLibraryItems(controlActionsList, LibControl.class);

        eventsList.setCellFactory(list_ -> new ListCell<NgmEntity.Event>() {
            @Override
            protected void updateItem(NgmEntity.Event item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    switch (item.getType()) {
                        case KEY_UP:
                        case KEY_DOWN:
                        case KEY_TAP:
                            setText(wordCamelCase(item.getType().toString()) + " " + keyCodeNames.get(item.getArgs()));
                            break;

                        case MOUSE_UP:
                        case MOUSE_TAP:
                        case MOUSE_DOWN:
                            switch (item.getArgs()) {
                                case "1": setText(wordCamelCase(item.getType().toString()) + " LEFT");   break;
                                case "2": setText(wordCamelCase(item.getType().toString()) + " RIGHT");  break;
                                case "3": setText(wordCamelCase(item.getType().toString()) + " MIDDLE"); break;
                            }
                            break;

                        default:
                            setText(wordCamelCase(item.getType().toString()) + " " + item.getArgs());
                    }
                } else {
                    setText("");
                }
            }
        });

        actionsList.setCellFactory(list_ -> new ListCell<NgmEntity.Event.Action>() {
            @Override
            protected void updateItem(NgmEntity.Event.Action item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(wordCamelCase(item.getCode()) + ": " + item.getArgs());
                } else {
                    setText("");
                }
            }
        });

        eventsList.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (currentEvent != null) {
                currentEvent.getActions().clear();
                currentEvent.getActions().addAll(actionsList.getItems());
            }

            currentEvent = n;

            if (currentEvent != null) {
                actionsList.getItems().clear();
                actionsList.getItems().addAll(currentEvent.getActions());
            }
        });
    }

    private void createActionLibraryItems(ListView<ActionDefinition<?>> list, Class<?> lib) {
        list.setCellFactory(list_ -> new ListCell<ActionDefinition<?>>() {
            @Override
            protected void updateItem(ActionDefinition<?> item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(wordCamelCase(item.getCode()).replaceFirst("[a-zA-Z]+\\s", ""));
                } else {
                    setText("");
                }
            }
        });

        for (Field field : lib.getFields()) {
            try {
                list.getItems().add((ActionDefinition<?>) field.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String wordCamelCase(String str) {
        String[] parts = str.toLowerCase().split("\\s+|_");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1);
        }

        return String.join(" ", parts);
    }
    
    private void createKeyMenu(Menu root, NgmEntity.Event.Type type) {
        final Function<JSONObject, MenuItem> createMenuItem = json -> {
            MenuItem menuItem = new MenuItem(json.get("key").getValue());

            keyCodeNames.put(String.valueOf(json.get("code").<Number> getValue().intValue()),
                    json.get("key").getValue());
            
            menuItem.setOnAction(actionEvent ->
                    System.out.println(String.format("Create %s event for key %s with code %d",
                            type,
                            json.get("key").getValue(),
                            json.get("code").<Number> getValue().intValue())));
            
            return menuItem;
        };

        final BiFunction<JSONArray, String, Menu> createMenu = (array, name) -> {
            Menu menu = new Menu(name);

            menu.getItems().addAll(
                    array.stream()
                            .map(JSONValue::<JSONObject> getValue)
                            .map(createMenuItem)
                            .collect(Collectors.toList())
            );

            return menu;
        };
        
        JSONArray arrowKeys = keyCodes.get("arrowKeys").getValue();
        JSONArray functionKeys = keyCodes.get("functionKeys").getValue();
        JSONArray alphabetKeys = keyCodes.get("alphabetKeys").getValue();
        JSONArray numberKeys = keyCodes.get("numberKeys").getValue();
        JSONArray otherKeys = keyCodes.get("otherKeys").getValue();
        JSONArray lockKeys = keyCodes.get("lockKeys").getValue();

        root.getItems().addAll(
                createMenu.apply(arrowKeys, "Arrow Keys"),
                createMenu.apply(functionKeys, "Function Keys"),
                createMenu.apply(alphabetKeys, "Alphabet Keys"),
                createMenu.apply(numberKeys, "Number Keys"),
                createMenu.apply(lockKeys, "Lock Keys"),
                createMenu.apply(otherKeys, "Other Keys")
        );
    }
}
