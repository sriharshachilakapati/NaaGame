package com.naagame.editor.controllers;

import com.naagame.core.io.ProjectReader;
import com.naagame.editor.io.ProjectWriter;
import com.naagame.core.NgmProject;
import com.naagame.core.resources.*;
import com.naagame.editor.util.RetentionFileChooser;
import com.shc.easyjson.ParseException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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

    private int resourceNum = 0;

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
            NgmProject.textures.add(new NgmTexture("Texture" + i));
            NgmProject.sprites.add(new NgmSprite("Sprite" + i));
            NgmProject.backgrounds.add(new NgmBackground("Background" + i));
            NgmProject.sounds.add(new NgmSound("Sound" + i));
            NgmProject.entities.add(new NgmEntity("Entity" + i));
            NgmProject.scenes.add(new NgmScene("Scene" + i));
        }

        refreshTreeUI();

        resourceTree.setOnMouseClicked(mouseEvent -> {
            switch (mouseEvent.getClickCount()) {
                case 2:
                    treeItemSelectionChanged(resourceTree.getSelectionModel().getSelectedItem());
                    break;

                case 3:
                    resourceTree.setEditable(true);
                    resourceTree.edit(resourceTree.getSelectionModel().getSelectedItem());
                    break;
            }
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

        ContextMenu resourceMenu = new ContextMenu();
        MenuItem renameMenuItem = new MenuItem("Rename");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        resourceMenu.getItems().addAll(renameMenuItem, deleteMenuItem);

        renameMenuItem.setOnAction(event -> {
            resourceTree.setEditable(true);
            resourceTree.edit(resourceTree.getSelectionModel().getSelectedItem());
        });
        deleteMenuItem.setOnAction(event -> {
            TreeItem<String> item = resourceTree.getSelectionModel().getSelectedItem();
            List<? extends IResource> resources = getResourceList(item);

            if (resources != null) {
                IResource resource = NgmProject.find(resources, item.getValue());
                resources.remove(resource);
                item.getParent().getChildren().remove(item);
            }
        });

        ContextMenu groupMenu = new ContextMenu();
        MenuItem createMenuItem = new MenuItem("Create");
        groupMenu.getItems().add(createMenuItem);
        createMenuItem.setOnAction(event -> {
            TreeItem<String> item = resourceTree.getSelectionModel().getSelectedItem();
            TreeItem<String> resourceItem = new TreeItem<>("");

            switch (item.getValue().toLowerCase()) {
                case "textures":
                    NgmProject.textures.add(new NgmTexture("Texture" + (++resourceNum)));
                    resourceItem.setValue("Texture" + resourceNum);
                    break;
                case "sprites":
                    NgmProject.sprites.add(new NgmSprite("Sprite" + (++resourceNum)));
                    resourceItem.setValue("Sprite" + resourceNum);
                    break;
                case "backgrounds":
                    NgmProject.backgrounds.add(new NgmBackground("Background" + (++resourceNum)));
                    resourceItem.setValue("Background" + resourceNum);
                    break;
                case "sounds":
                    NgmProject.sounds.add(new NgmSound("Sound" + (++resourceNum)));
                    resourceItem.setValue("Sound" + resourceNum);
                    break;
                case "entities":
                    NgmProject.entities.add(new NgmEntity("Entity" + (++resourceNum)));
                    resourceItem.setValue("Entity" + resourceNum);
                    break;
                case "scenes":
                    NgmProject.scenes.add(new NgmScene("Scene" + (++resourceNum)));
                    resourceItem.setValue("Scene" + resourceNum);
                    break;
            }

            item.getChildren().add(resourceItem);
            item.setExpanded(true);
            resourceTree.setEditable(true);
            resourceTree.layout();
            resourceTree.getSelectionModel().select(resourceItem);
            resourceTree.edit(resourceItem);
        });

        Callback<TreeView<String>, TreeCell<String>> cellFactory = TextFieldTreeCell.forTreeView();
        resourceTree.setCellFactory(tv -> {
            TreeCell<String> cell = cellFactory.call(tv);

            cell.treeItemProperty().addListener((value, oldItem, newItem) -> {
                cell.setEditable(false);
                cell.setContextMenu(null);

                if (newItem != null) {
                    cell.setContextMenu(groupMenu);

                    if (newItem.getParent().getValue() != null) {
                        cell.setEditable(true);
                        cell.setContextMenu(resourceMenu);
                    }
                }
            });

            return cell;
        });

        resourceTree.setOnEditCommit(event -> {
            List<? extends IResource> resources = getResourceList(event.getTreeItem());

            if (resources != null) {
                if (!event.getNewValue().trim().equals("") && NgmProject.find(resources, event.getNewValue()) == null) {
                    IResource resource = NgmProject.find(resources, event.getOldValue());
                    resource.setName(event.getNewValue().trim());
                } else {
                    Platform.runLater(() -> event.getTreeItem().setValue(event.getOldValue()));
                }
            }

            resourceTree.setEditable(false);
        });

        resourceTree.setOnEditCancel(event -> resourceTree.setEditable(false));
    }

    private List<? extends IResource> getResourceList(TreeItem<String> item) {
        switch (item.getParent().getValue().toLowerCase()) {
            case "textures":    return NgmProject.textures;
            case "sprites":     return NgmProject.sprites;
            case "backgrounds": return NgmProject.backgrounds;
            case "sounds":      return NgmProject.sounds;
            case "entities":    return NgmProject.entities;
            case "scenes":      return NgmProject.scenes;
        }

        return null;
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

        addAll.accept(NgmProject.textures, textures);
        addAll.accept(NgmProject.sprites, sprites);
        addAll.accept(NgmProject.backgrounds, backgrounds);
        addAll.accept(NgmProject.sounds, sounds);
        addAll.accept(NgmProject.entities, entities);
        addAll.accept(NgmProject.scenes, scenes);
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
            String json = new String(Files.readAllBytes(path));
            ProjectReader.loadFromJSON(json);
            refreshTreeUI();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
