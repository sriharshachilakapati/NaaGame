package com.naagame.editor.controllers;

import com.naagame.editor.model.Resources;
import com.naagame.editor.model.resources.*;
import com.shc.easyjson.JSON;
import com.shc.easyjson.JSONObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class MainController implements Initializable, IController {

    @FXML public ScrollPane content;
    @FXML public TreeView<String> resourceTree;

    public Stage stage;

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

    private TextureEditorController textureEditorController;
    private SpriteEditorController spriteEditorController;
    private BackgroundEditorController backgroundEditorController;
    private SoundEditorController soundEditorController;
    private EntityEditorController entityEditorController;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<String> root = new TreeItem<>();

        textures = new TreeItem<>("Textures");
        sprites = new TreeItem<>("Sprites");
        backgrounds = new TreeItem<>("Backgrounds");
        sounds = new TreeItem<>("Sounds");
        entities = new TreeItem<>("Entities");
        scenes = new TreeItem<>("Scenes");

        root.getChildren().addAll(textures, sprites, backgrounds, sounds, entities, scenes);

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

        this.<SpriteEditorController>createEditor("sprite.fxml", (editor, controller) -> {
            spriteEditor = editor;
            spriteEditorController = controller;
        });

        this.<TextureEditorController>createEditor("texture.fxml", (editor, controller) -> {
            textureEditor = editor;
            textureEditorController = controller;
        });

        this.<BackgroundEditorController>createEditor("background.fxml", (editor, controller) -> {
            backgroundEditor = editor;
            backgroundEditorController = controller;
        });

        this.<SoundEditorController>createEditor("sound.fxml", (editor, controller) -> {
            soundEditor = editor;
            soundEditorController = controller;
        });

        this.<EntityEditorController>createEditor("entity.fxml", (editor, controller) -> {
            entityEditor = editor;
            entityEditorController = controller;
        });
    }

    private <T> void createEditor(String name, BiConsumer<Pane, T> consumer) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource(name)));
            Pane editor = loader.load();
            T controller = loader.getController();
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
                textureEditorController.currentTexture = item.getValue();
                textureEditorController.init();
                content.setContent(textureEditor);
                break;

            case "sprites":
                spriteEditorController.currentSprite = item.getValue();
                spriteEditorController.init();
                content.setContent(spriteEditor);
                break;

            case "backgrounds":
                backgroundEditorController.currentBackground = item.getValue();
                backgroundEditorController.init();
                content.setContent(backgroundEditor);
                break;

            case "sounds":
                soundEditorController.currentSound = item.getValue();
                soundEditorController.init();
                content.setContent(soundEditor);
                break;

            case "entities":
                entityEditorController.currentEntity = item.getValue();
                entityEditorController.init();
                content.setContent(entityEditor);
                break;

            case "scenes":
                break;
        }
    }

    @FXML
    public void onSaveMenuItemClicked() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("NaaGame Projects", "*.ngm"));
        chooser.setTitle("Save Project As");

        File selected = chooser.showSaveDialog(stage);
        if (!selected.getAbsolutePath().endsWith(".ngm")) {
            selected = new File(selected.getAbsolutePath() + ".ngm");
        }

        JSONObject project = Resources.exportToJSON();

        String jsonString = JSON.write(project);
        try {
            Files.write(selected.toPath(), jsonString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
