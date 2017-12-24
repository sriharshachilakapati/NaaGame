package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class MainController implements Initializable {

    @FXML public ScrollPane content;
    @FXML public TreeView<String> resourceTree;

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

    private TextureEditorController textureEditorController;
    private SpriteEditorController spriteEditorController;
    private BackgroundEditorController backgroundEditorController;
    private SoundEditorController soundEditorController;

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
            Resource.textures.add(new Resource.Texture("Texture" + i));
            Resource.sprites.add(new Resource.Sprite("Sprite" + i));
            Resource.backgrounds.add(new Resource.Background("Background" + i));
            Resource.sounds.add(new Resource.Sound("Sound" + i));
            Resource.entities.add(new Resource.Entity("Entity" + i));
            Resource.scenes.add(new Resource.Scene("Scene" + i));
        }

        refreshTreeUI();

        resourceTree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2)
                treeItemSelectionChanged(resourceTree.getSelectionModel().getSelectedItem());
        });

        this.<Pane, SpriteEditorController> createEditor("sprite.fxml", (editor, controller) -> {
            spriteEditor = editor;
            spriteEditorController = controller;
        });

        this.<Pane, TextureEditorController> createEditor("texture.fxml", (editor, controller) -> {
            textureEditor = editor;
            textureEditorController = controller;
        });

        this.<Pane, BackgroundEditorController> createEditor("background.fxml", (editor, controller) -> {
            backgroundEditor = editor;
            backgroundEditorController = controller;
        });

        this.<Pane, SoundEditorController> createEditor("sound.fxml", (editor, controller) -> {
            soundEditor = editor;
            soundEditorController = controller;
        });
    }

    private <E, T> void createEditor(String name, BiConsumer<E, T> consumer) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource(name)));
            E editor = loader.load();
            T controller = loader.getController();
            consumer.accept(editor, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTreeUI() {
        resourceTree.getRoot().getChildren().forEach(c -> c.getChildren().clear());

        final BiConsumer<List<? extends Resource>, TreeItem<String>> addAll = (l, r) ->
                l.forEach(v -> {
                    TreeItem<String> item = new TreeItem<>(v.name);
                    r.getChildren().add(item);
                });

        addAll.accept(Resource.textures, textures);
        addAll.accept(Resource.sprites, sprites);
        addAll.accept(Resource.backgrounds, backgrounds);
        addAll.accept(Resource.sounds, sounds);
        addAll.accept(Resource.entities, entities);
        addAll.accept(Resource.scenes, scenes);
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
                break;

            case "scenes":
                break;
        }
    }
}
