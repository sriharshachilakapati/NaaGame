package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.control.*;
import com.naagame.core.action.debug.LibDebug;
import com.naagame.core.action.movement.LibMovement;
import com.naagame.core.resources.IResource;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import com.naagame.editor.util.ActionEditor;
import com.naagame.editor.util.EntityActionListCell;
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
    @FXML private Menu addEvtMousePressedMenu;
    @FXML private Menu addEvtMouseReleasedMenu;
    @FXML private Menu addEvtMouseTappedMenu;
    @FXML private Menu addEvtCollisionMenu;
    @FXML private Menu addEvtNoneExistsMenu;

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
            eventsList.getSelectionModel().select(currentEvent);
        }

        resourcesChanged();
        changed = false;
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

    private void addEvent(NgmEntity.Event.Type type, String... args) {
        String allArgs = String.join("", args);

        boolean alreadyAdded = eventsList.getItems().stream()
                .anyMatch(event -> event.getType() == type && event.getArgs().equals(allArgs));

        NgmEntity.Event event;

        if (!alreadyAdded) {
            event = new NgmEntity.Event(type, allArgs);
            eventsList.getItems().add(event);
        } else {
            event = eventsList.getItems().stream()
                    .filter(e -> e.getType() == type && e.getArgs().equals(allArgs))
                    .findFirst()
                    .orElse(null);
        }

        eventsList.getSelectionModel().select(event);
        changed = true;
    }

    @FXML
    private void addCreateEvent() {
        addEvent(NgmEntity.Event.Type.CREATE);
    }

    @FXML
    private void addUpdateEvent() {
        addEvent(NgmEntity.Event.Type.UPDATE);
    }

    @FXML
    private void addOutOfBoundsEvent() {
        addEvent(NgmEntity.Event.Type.OUT_OF_BOUNDS);
    }

    @FXML
    private void addDestroyEvent() {
        addEvent(NgmEntity.Event.Type.DESTROY);
    }

    @FXML
    private void addEvtNoMoreLives() {
        addEvent(NgmEntity.Event.Type.NO_MORE_LIVES);
    }

    @FXML
    private void onDeleteEventClicked() {
        NgmEntity.Event selected;

        if ((selected = eventsList.getSelectionModel().getSelectedItem()) != null) {
            eventsList.getItems().remove(selected);
            changed = true;
        }
    }

    @FXML
    @Override
    protected void discardChanges() {
        init(entity.getName());
    }

    @Override
    protected void resourcesChanged() {
        createCollisionMenu();
        createNoneExistsMenu();

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

        eventsList.getItems().removeIf(
                event -> (event.getType() == NgmEntity.Event.Type.COLLISION ||
                        event.getType() == NgmEntity.Event.Type.NONE_EXISTS) &&
                        NgmProject.find(NgmProject.entities, event.getArgs()) == null
        );

        for (NgmEntity.Event event : eventsList.getItems()) {
            event.getActions().removeIf(action -> {
                if (action.getCode().equals(LibControl.CREATE_INSTANCE.getCode())) {
                    CreateInstance object = LibControl.CREATE_INSTANCE.decode(action.getArgs(),
                            LibControl.CREATE_INSTANCE.getSupplier().get());

                    return NgmProject.find(NgmProject.entities, object.getEntity().getName()) == null;

                } else if (action.getCode().equals(LibControl.GOTO_SCENE.getCode())) {
                    GotoScene object = LibControl.GOTO_SCENE.decode(action.getArgs(),
                            LibControl.GOTO_SCENE.getSupplier().get());

                    return NgmProject.find(NgmProject.scenes, object.getScene().getName()) == null;

                } else if (action.getCode().equals(LibControl.PLAY_SOUND.getCode())) {
                    PlaySound object = LibControl.PLAY_SOUND.decode(action.getArgs(),
                            LibControl.PLAY_SOUND.getSupplier().get());

                    return NgmProject.find(NgmProject.sounds, object.getSound().getName()) == null;

                } else if (action.getCode().equals(LibControl.STOP_SOUND.getCode())) {
                    StopSound object = LibControl.STOP_SOUND.decode(action.getArgs(),
                            LibControl.STOP_SOUND.getSupplier().get());

                    return NgmProject.find(NgmProject.sounds, object.getSound().getName()) == null;
                }

                return false;
            });
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

        if (currentEvent != null) {
            currentEvent.getActions().clear();
            currentEvent.getActions().addAll(actionsList.getItems());
        }

        entity.getEvents().clear();
        entity.getEvents().addAll(eventsList.getItems().stream()
                .map(this::cloneEvent).collect(Collectors.toList()));

        notifySave();
        changed = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spriteSelector.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (n != null) {
                NgmSprite newSprite = NgmProject.find(NgmProject.sprites, n);
                changed = newSprite != sprite;
                sprite = newSprite;
            }
        });

        createKeyMenu(addEvtKeyPressedMenu, NgmEntity.Event.Type.KEY_DOWN);
        createKeyMenu(addEvtKeyReleasedMenu, NgmEntity.Event.Type.KEY_UP);
        createKeyMenu(addEvtKeyTappedMenu, NgmEntity.Event.Type.KEY_TAP);

        createMouseMenu(addEvtMousePressedMenu, NgmEntity.Event.Type.MOUSE_DOWN);
        createMouseMenu(addEvtMouseReleasedMenu, NgmEntity.Event.Type.MOUSE_UP);
        createMouseMenu(addEvtMouseTappedMenu, NgmEntity.Event.Type.MOUSE_TAP);

        createCollisionMenu();
        createNoneExistsMenu();

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

        actionsList.setCellFactory(list_ -> new EntityActionListCell(item ->
                        wordCamelCase(item.getCode()) + ": " + item.getArgs(), () -> changed = true));

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

    @SuppressWarnings("unchecked")
    private void createActionLibraryItems(ListView<ActionDefinition<?>> list, Class<?> lib) {
        list.setCellFactory(list_ -> new ListCell<ActionDefinition<?>>() {
            {
                setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2) {
                        ActionDefinition<Object> definition = (ActionDefinition<Object>) getItem();
                        NgmEntity.Event.Action action = new NgmEntity.Event.Action(
                                definition.getCode(),
                                definition.encode(definition.getSupplier().get())
                        );

                        if (ActionEditor.edit(definition, action)) {
                            actionsList.getItems().add(action);
                            changed = true;
                        }
                    }
                });
            }

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
            String code = String.valueOf(json.get("code").<Number> getValue().intValue());

            keyCodeNames.put(code, json.get("key").getValue());
            menuItem.setOnAction(actionEvent -> addEvent(type, code));
            
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

    private void createMouseMenu(Menu root, NgmEntity.Event.Type type) {
        final Function<String, MenuItem> createMenuItem = code -> {
            MenuItem item = new MenuItem();

            switch (code) {
                case "1": item.setText("LEFT");   break;
                case "2": item.setText("RIGHT");  break;
                case "3": item.setText("MIDDLE"); break;
            }

            item.setOnAction(event -> addEvent(type, code));

            return item;
        };

        root.getItems().addAll(
                createMenuItem.apply("1"),
                createMenuItem.apply("3"),
                createMenuItem.apply("2")
        );
    }

    private void createCollisionMenu() {
        final Function<String, MenuItem> createMenuItem = entityName -> {
            MenuItem item = new MenuItem(entityName);
            item.setOnAction(event -> addEvent(NgmEntity.Event.Type.COLLISION, entityName));

            return item;
        };

        addEvtCollisionMenu.getItems().clear();
        addEvtCollisionMenu.getItems().addAll(NgmProject.entities.stream()
                .map(IResource::getName)
                .map(createMenuItem)
                .collect(Collectors.toList()));
    }

    private void createNoneExistsMenu() {
        final Function<String, MenuItem> createMenuItem = entityName -> {
            MenuItem item = new MenuItem(entityName);
            item.setOnAction(event -> addEvent(NgmEntity.Event.Type.NONE_EXISTS, entityName));

            return item;
        };

        addEvtNoneExistsMenu.getItems().clear();
        addEvtNoneExistsMenu.getItems().addAll(NgmProject.entities.stream()
                .map(IResource::getName)
                .map(createMenuItem)
                .collect(Collectors.toList()));
    }
}
