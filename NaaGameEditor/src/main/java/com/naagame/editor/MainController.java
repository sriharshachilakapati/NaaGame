package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML public Pane content;
    @FXML public TreeView<String> resourceTree;

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<String> root = new TreeItem<>();
        TreeItem<String> textures = new TreeItem<>("Textures");
        TreeItem<String> sprites = new TreeItem<>("Sprites");
        TreeItem<String> backgrounds = new TreeItem<>("Backgrounds");
        TreeItem<String> sounds = new TreeItem<>("Sounds");
        TreeItem<String> entities = new TreeItem<>("Entities");
        TreeItem<String> scenes = new TreeItem<>("Scenes");

        root.getChildren().addAll(textures, sprites, backgrounds, sounds, entities, scenes);

        root.getChildren().forEach(item -> {
            for (int i = 0; i < 10; i++)
                item.getChildren().add(new TreeItem<>(String.format("%s%d", item.getValue(), i + 1)));
        });

        resourceTree.setRoot(root);
    }
}
