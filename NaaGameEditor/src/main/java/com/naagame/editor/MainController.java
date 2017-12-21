package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class MainController implements Initializable {

    @FXML public Pane content;
    @FXML public TreeView<String> resourceTree;

    private TreeItem<String> textures;
    private TreeItem<String> sprites;
    private TreeItem<String> backgrounds;
    private TreeItem<String> sounds;
    private TreeItem<String> entities;
    private TreeItem<String> scenes;

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
}
