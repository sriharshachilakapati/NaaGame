package com.naagame.editor.controllers;

import com.naagame.editor.io.ProjectReader;
import com.naagame.editor.io.ProjectWriter;
import com.naagame.editor.model.Resources;
import com.naagame.editor.model.resources.*;
import com.naagame.editor.util.RetentionFileChooser;
import com.shc.easyjson.ParseException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import static com.naagame.editor.util.RetentionFileChooser.EXTENSION_FILTER_NAAGAME_PROJ;

public class MainController implements Initializable, IController {

    @FXML public ScrollPane content;
    @FXML public TreeView<String> resourceTree;

    private Window window;

    private TreeItem<String> textures;
    private TreeItem<String> sprites;
    private TreeItem<String> backgrounds;
    private TreeItem<String> sounds;
    private TreeItem<String> entities;
    private TreeItem<String> scenes;

    private Pane textureEditor;
    private Pane spriteEditor;
    private Pane backgroundEditor;
    private Pane soundEditor;
    private Pane entityEditor;

    private IController textureEditorController;
    private IController spriteEditorController;
    private IController backgroundEditorController;
    private IController soundEditorController;
    private IController entityEditorController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> window = content.getScene().getWindow());

        TreeItem<String> root = new TreeItem<>();

        root.getChildren().add(textures = new TreeItem<>("Textures"));
        root.getChildren().add(sprites = new TreeItem<>("Sprites"));
        root.getChildren().add(backgrounds = new TreeItem<>("Backgrounds"));
        root.getChildren().add(sounds = new TreeItem<>("Sounds"));
        root.getChildren().add(entities = new TreeItem<>("Entities"));
        root.getChildren().add(scenes = new TreeItem<>("Scenes"));

        resourceTree.setRoot(root);

        for (int i = 1; i < 11; i++) {
            Resources.textures.add(new Texture("Texture" + i));
            Resources.sprites.add(new Sprite("Sprite" + i));
            Resources.backgrounds.add(new Background("Background" + i));
            Resources.sounds.add(new Sound("Sound" + i));
            Resources.entities.add(new Entity("Entity" + i));
            Resources.scenes.add(new Scene("Scene" + i));
        }

        refreshTreeUI();

        resourceTree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2)
                treeItemSelectionChanged(resourceTree.getSelectionModel().getSelectedItem());
        });

        createEditor("sprite.fxml", (editor, controller) -> {
            spriteEditor = editor;
            spriteEditorController = controller;
        });

        createEditor("texture.fxml", (editor, controller) -> {
            textureEditor = editor;
            textureEditorController = controller;
        });

        createEditor("background.fxml", (editor, controller) -> {
            backgroundEditor = editor;
            backgroundEditorController = controller;
        });

        createEditor("sound.fxml", (editor, controller) -> {
            soundEditor = editor;
            soundEditorController = controller;
        });

        createEditor("entity.fxml", (editor, controller) -> {
            entityEditor = editor;
            entityEditorController = controller;
        });
    }

    private void createEditor(String name, BiConsumer<Pane, IController> consumer) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource(name)));
            Pane editor = loader.load();
            IController controller = loader.getController();
            consumer.accept(editor, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTreeUI() {
        resourceTree.getRoot().getChildren().forEach(c -> c.getChildren().clear());

        final BiConsumer<List<? extends IResource>, TreeItem<String>> addAll = (l, r) ->
                l.forEach(v -> {
                    TreeItem<String> item = new TreeItem<>(v.getName());
                    r.getChildren().add(item);
                });

        addAll.accept(Resources.textures, textures);
        addAll.accept(Resources.sprites, sprites);
        addAll.accept(Resources.backgrounds, backgrounds);
        addAll.accept(Resources.sounds, sounds);
        addAll.accept(Resources.entities, entities);
        addAll.accept(Resources.scenes, scenes);
    }

    private void treeItemSelectionChanged(TreeItem<String> item) {
        if (item.getParent().getValue() == null)
            return;

        switch (item.getParent().getValue().toLowerCase()) {
            case "textures":
                textureEditorController.init(item.getValue());
                content.setContent(textureEditor);
                break;

            case "sprites":
                spriteEditorController.init(item.getValue());
                content.setContent(spriteEditor);
                break;

            case "backgrounds":
                backgroundEditorController.init(item.getValue());
                content.setContent(backgroundEditor);
                break;

            case "sounds":
                soundEditorController.init(item.getValue());
                content.setContent(soundEditor);
                break;

            case "entities":
                entityEditorController.init(item.getValue());
                content.setContent(entityEditor);
                break;

            case "scenes":
                break;
        }
    }

    @FXML
    public void onSaveMenuItemClicked() {
        Path path = RetentionFileChooser.showSaveDialog(window);

        if (path == null) {
            return;
        }

        try {
            ProjectWriter.writeToFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOpenMenuItemClicked() {
        Path path = RetentionFileChooser.showOpenDialog(window, EXTENSION_FILTER_NAAGAME_PROJ);

        if (path == null) {
            return;
        }

        try {
            ProjectReader.loadFromFile(path);
            refreshTreeUI();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
