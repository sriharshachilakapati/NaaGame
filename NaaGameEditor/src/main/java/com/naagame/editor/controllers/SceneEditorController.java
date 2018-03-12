package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.IResource;
import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmScene;
import com.naagame.editor.util.LevelEditor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SceneEditorController extends Controller implements Initializable {

    @FXML private Canvas sceneCanvas;

    @FXML private Spinner<Integer> spinnerWidth;
    @FXML private Spinner<Integer> spinnerHeight;

    @FXML private ChoiceBox<String> entitySelector;

    private LevelEditor levelEditor;

    private List<NgmScene.Instance<NgmBackground>> backgrounds;
    private List<NgmScene.Instance<NgmEntity>> entities;

    private <T extends IResource> NgmScene.Instance<T> cloneInstance(NgmScene.Instance<T> instance) {
        return new NgmScene.Instance<>(instance.getObject(), instance.getPosX(), instance.getPosY());
    }

    @Override
    public void init(String name) {
        NgmScene scene = NgmProject.find(NgmProject.scenes, name);

        backgrounds = new ArrayList<>();
        backgrounds.addAll(scene.getBackgrounds().stream().map(this::cloneInstance).collect(Collectors.toList()));

        entities = new ArrayList<>();
        entities.addAll(scene.getEntities().stream().map(this::cloneInstance).collect(Collectors.toList()));

        levelEditor = new LevelEditor(this, sceneCanvas);
        levelEditor.redraw();

        resourcesChanged();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinnerHeight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50000, 600));
        spinnerWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50000, 800));

        spinnerWidth.valueProperty().addListener((ov, o, n) -> {
            sceneCanvas.setWidth(n);
            levelEditor.redraw();
        });
        spinnerHeight.valueProperty().addListener((ov, o, n) -> {
            sceneCanvas.setHeight(n);
            levelEditor.redraw();
        });

        spinnerWidth.focusedProperty().addListener((ov, o, n) -> spinnerWidth.increment(0));
        spinnerHeight.focusedProperty().addListener((ov, o, n) -> spinnerHeight.increment(0));

        sceneCanvas.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    String selected = getSelectedEntity();

                    if (selected == null) {
                        break;
                    }

                    entities.add(new NgmScene.Instance<>(NgmProject.find(NgmProject.entities, selected),
                            (float) event.getX(),
                            (float) event.getY()));

                    levelEditor.redraw();
                    break;

                case SECONDARY:
                    break;
            }
        });
    }

    @Override
    protected void resourcesChanged() {
        entitySelector.getItems().clear();
        entitySelector.getItems().addAll(NgmProject.entities.stream()
                .map(IResource::getName)
                .collect(Collectors.toList()));
    }

    public String getSelectedEntity() {
        return entitySelector.getSelectionModel().getSelectedItem();
    }

    public List<NgmScene.Instance<NgmBackground>> getBackgrounds() {
        return backgrounds;
    }

    public List<NgmScene.Instance<NgmEntity>> getEntities() {
        return entities;
    }
}
