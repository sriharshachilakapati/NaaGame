package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.io.ProjectReader;
import com.naagame.core.resources.*;
import com.naagame.editor.Main;
import com.naagame.editor.io.ProjectWriter;
import com.naagame.editor.util.PathResolver;
import com.naagame.editor.util.ProjectUtils;
import com.naagame.editor.util.RetentionFileChooser;
import com.shc.easyjson.ParseException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;

import static com.naagame.editor.util.RetentionFileChooser.EXTENSION_FILTER_NAAGAME_PROJ;

public class MainController extends Controller implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private TreeView<String> resourceTree;

    private TreeItem<String> textures;
    private TreeItem<String> sprites;
    private TreeItem<String> backgrounds;
    private TreeItem<String> sounds;
    private TreeItem<String> entities;
    private TreeItem<String> scenes;

    private Map<String, Pair<Tab, Controller>> tabMap;

    private int resourceNum = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabMap = new HashMap<>();

        TreeItem<String> root = new TreeItem<>();

        root.getChildren().add(textures = new TreeItem<>("Textures"));
        root.getChildren().add(sprites = new TreeItem<>("Sprites"));
        root.getChildren().add(backgrounds = new TreeItem<>("Backgrounds"));
        root.getChildren().add(sounds = new TreeItem<>("Sounds"));
        root.getChildren().add(entities = new TreeItem<>("Entities"));
        root.getChildren().add(scenes = new TreeItem<>("Scenes"));

        resourceTree.setRoot(root);

//        for (int i = 1; i < 11; i++) {
//            NgmProject.textures.add(new NgmTexture("Texture" + i));
//            NgmProject.sprites.add(new NgmSprite("Sprite" + i));
//            NgmProject.backgrounds.add(new NgmBackground("Background" + i));
//            NgmProject.sounds.add(new NgmSound("Sound" + i));
//            NgmProject.entities.add(new NgmEntity("Entity" + i));
//            NgmProject.scenes.add(new NgmScene("Scene" + i));
//
//            resourceNum += 6;
//        }

        refreshTreeUI();

        resourceTree.setOnMouseClicked(mouseEvent -> {
            switch (mouseEvent.getClickCount()) {
                case 2:
                    openResource(resourceTree.getSelectionModel().getSelectedItem());
                    break;

                case 3:
                    resourceTree.setEditable(true);
                    resourceTree.edit(resourceTree.getSelectionModel().getSelectedItem());
                    break;
            }
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

                String tabKey = item.getParent().getValue() + "@" + item.getValue();

                Pair<Tab, Controller> pair = tabMap.get(tabKey);

                if (pair != null) {
                    pair.getValue().discardChanges();
                    tabPane.getTabs().remove(pair.getKey());
                }

                resources.remove(resource);
                item.getParent().getChildren().remove(item);

                ProjectUtils.pruneDeadReferences();
            }

            resourcesChanged();
        });

        ContextMenu groupMenu = new ContextMenu();
        MenuItem createMenuItem = new MenuItem("Create");
        groupMenu.getItems().add(createMenuItem);
        createMenuItem.setOnAction(event -> {
            TreeItem<String> item = resourceTree.getSelectionModel().getSelectedItem();

            switch (item.getValue().toLowerCase()) {
                case "textures":    onCreateTextureClicked();    break;
                case "sprites":     onCreateSpriteClicked();     break;
                case "backgrounds": onCreateBackgroundClicked(); break;
                case "sounds":      onCreateSoundClicked();      break;
                case "entities":    onCreateEntityClicked();     break;
                case "scenes":      onCreateSceneClicked();      break;
            }
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

                    TreeItem<String> item = event.getTreeItem();
                    String tabKey = item.getParent().getValue() + "@" + event.getOldValue();

                    Pair<Tab, Controller> pair = tabMap.get(tabKey);

                    if (pair != null) {
                        tabMap.remove(tabKey);

                        tabKey = item.getParent().getValue() + "@" + event.getNewValue();
                        tabMap.put(tabKey, pair);

                        Platform.runLater(() -> {
                            pair.getKey().setText(event.getNewValue());
                            resourceTree.getSelectionModel().select(null);
                        });
                    }

                    resourcesChanged();
                } else {
                    Platform.runLater(() -> event.getTreeItem().setValue(event.getOldValue()));
                }
            }

            resourceTree.setEditable(false);
        });

        resourceTree.setOnEditCancel(event -> resourceTree.setEditable(false));
    }

    private void resourceCreated(TreeItem<String> item, TreeItem<String> parent) {
        parent.getChildren().add(item);
        parent.setExpanded(true);

        resourceTree.setEditable(true);
        resourceTree.layout();
        resourceTree.getSelectionModel().select(item);
        resourceTree.edit(item);

        resourcesChanged();
    }

    @FXML
    public void onCreateSceneClicked() {
        TreeItem<String> item = new TreeItem<>("");
        NgmProject.scenes.add(new NgmScene("Scene" + (++resourceNum)));
        item.setValue("Scene" + resourceNum);

        resourceCreated(item, scenes);
    }

    @FXML
    public void onCreateEntityClicked() {
        TreeItem<String> item = new TreeItem<>("");
        NgmProject.entities.add(new NgmEntity("Entity" + (++resourceNum)));
        item.setValue("Entity" + resourceNum);

        resourceCreated(item, entities);
    }

    @FXML
    public void onCreateSoundClicked() {
        TreeItem<String> item = new TreeItem<>("");
        NgmProject.sounds.add(new NgmSound("Sound" + (++resourceNum)));
        item.setValue("Sound" + resourceNum);

        resourceCreated(item, sounds);
    }

    @FXML
    public void onCreateBackgroundClicked() {
        TreeItem<String> item = new TreeItem<>("");
        NgmProject.backgrounds.add(new NgmBackground("Background" + (++resourceNum)));
        item.setValue("Background" + resourceNum);

        resourceCreated(item, backgrounds);
    }

    @FXML
    public void onCreateSpriteClicked() {
        TreeItem<String> item = new TreeItem<>("");
        NgmProject.sprites.add(new NgmSprite("Sprite" + (++resourceNum)));
        item.setValue("Sprite" + resourceNum);

        resourceCreated(item, sprites);
    }

    @FXML
    public void onCreateTextureClicked() {
        TreeItem<String> item = new TreeItem<>("");
        NgmProject.textures.add(new NgmTexture("Texture" + (++resourceNum)));
        item.setValue("Texture" + resourceNum);

        resourceCreated(item, textures);
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

    private Pair<Tab, Controller> createEditor(TreeItem<String> item) {
        try {
            String name;

            switch (item.getParent().getValue().toLowerCase()) {
                case "textures":    name = "texture.fxml";    break;
                case "sprites":     name = "sprite.fxml";     break;
                case "backgrounds": name = "background.fxml"; break;
                case "sounds":      name = "sound.fxml";      break;
                case "entities":    name = "entity.fxml";     break;
                case "scenes":      name = "scene.fxml";      break;

                default: return null;
            }

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource(name)));

            Pane editor = loader.load();
            Controller controller = loader.getController();

            Tab tab = new Tab();
            tab.setContent(editor);

            if (controller != null) {
                controller.setMainController(this);
                controller.init(item.getValue());

                tab.setOnCloseRequest(event -> {
                    if (!controller.hasUnsavedEdits()) {
                        return;
                    }

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(Main.window);
                    alert.setTitle("NaaGame");
                    alert.setHeaderText("You have some unsaved changes!");
                    alert.setContentText("Do you want to save your changes to " + tab.getText() + "?");

                    ButtonType yesBtn = new ButtonType("Yes");
                    ButtonType noBtn = new ButtonType("No");
                    ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().clear();
                    alert.getButtonTypes().addAll(yesBtn, noBtn, cancelBtn);

                    Optional<ButtonType> result = alert.showAndWait();

                    ButtonType selected = result.orElse(cancelBtn);

                    if (selected == yesBtn) {
                        controller.commitChanges();
                    } else if (selected == noBtn) {
                        controller.discardChanges();
                    } else {
                        event.consume();
                    }
                });
            }

            return new Pair<>(tab, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    private void openResource(TreeItem<String> item) {
        if (item.getParent().getValue() == null)
            return;

        String tabKey = item.getParent().getValue() + "@" + item.getValue();
        Pair<Tab, Controller> pair = tabMap.get(tabKey);

        if (pair == null) {
            Objects.requireNonNull(pair = createEditor(item));

            Tab tab = pair.getKey();

            tab.setOnClosed(e -> tabMap.remove(tabKey));
            tab.setText(item.getValue());

            tabMap.put(tabKey, pair);
            tabPane.getTabs().add(pair.getKey());
        }

        tabPane.getSelectionModel().select(pair.getKey());
    }

    @FXML
    public void onSaveMenuItemClicked() {
        Path path = RetentionFileChooser.showSaveDialog();

        if (path == null) {
            return;
        }

        try {
            ProjectUtils.pruneDeadReferences();
            ProjectWriter.writeToFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOpenMenuItemClicked() {
        Path path = RetentionFileChooser.showOpenDialog(EXTENSION_FILTER_NAAGAME_PROJ);

        if (path == null) {
            return;
        }

        try {
            String json = new String(Files.readAllBytes(path));
            ProjectReader.loadFromJSON(json);
            refreshTreeUI();

            tabPane.getTabs().clear();
            tabMap.clear();

            PathResolver.setProjectDir(path.getParent());
            ProjectUtils.pruneDeadReferences();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onNewProjectClicked() {
        PathResolver.setProjectDir(null);

        NgmProject.textures.clear();
        NgmProject.sprites.clear();
        NgmProject.backgrounds.clear();
        NgmProject.sounds.clear();
        NgmProject.entities.clear();
        NgmProject.scenes.clear();

        tabMap.clear();
        tabPane.getTabs().clear();

        refreshTreeUI();
    }

    @FXML
    public void onPlayButtonClicked() {
        try {
            Path tempDir = Files.createTempDirectory("NaaGame");
            Path projectFile = tempDir.resolve("temp.ngm");

            ProjectWriter.writeToFile(projectFile);

            ProcessBuilder processBuilder = new ProcessBuilder();

            Path playerPath;

            if (Files.exists(Paths.get("NaaGamePlayer.jar"))) {
                playerPath = Paths.get("NaaGamePlayer.jar").toAbsolutePath();
            } else {
                playerPath = Paths.get("NaaGamePlayerDesktop/build/libs/NaaGamePlayerDesktop.jar").toAbsolutePath();
            }

            String[] commands = {
                    "java",
                    "-jar",
                    playerPath.toString(),
                    projectFile.toString()
            };

            Process playerProcess = processBuilder.command(commands).start();
            playerProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void resourcesChanged() {
        Platform.runLater(() -> tabMap.values().forEach(p -> p.getValue().resourcesChanged()));
    }
}
