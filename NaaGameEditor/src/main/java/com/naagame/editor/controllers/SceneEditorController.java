package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.*;
import com.naagame.editor.util.ImageCache;
import com.naagame.editor.util.LevelEditor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
            String selected = getSelectedEntity();

            if (selected == null) {
                return;
            }

            switch (event.getButton()) {
                case PRIMARY:
                    entities.add(new NgmScene.Instance<>(NgmProject.find(NgmProject.entities, selected),
                            (float) event.getX(),
                            (float) event.getY()));
                    break;

                case SECONDARY:
                    Collections.reverse(entities);
                    entities.stream()
                            .filter(entityInstance -> getEntityBounds(entityInstance).contains(event.getX(), event.getY()))
                            .findFirst()
                            .ifPresent(entities::remove);
                    Collections.reverse(entities);
                    break;
            }

            levelEditor.redraw();
        });
    }

    private Rectangle getEntityBounds(NgmScene.Instance<NgmEntity> entityInstance) {
        Rectangle rectangle = new Rectangle();

        NgmEntity entity = entityInstance.getObject();
        NgmSprite sprite = entity.getSprite();

        if (sprite == null || sprite.getFrames().size() == 0 || sprite.getFrames().get(0).getTexture() == null) {
            rectangle.setX(entityInstance.getPosX());
            rectangle.setY(entityInstance.getPosY());
            rectangle.setWidth(0);
            rectangle.setHeight(0);

            return rectangle;
        }

        NgmTexture texture = sprite.getFrames().get(0).getTexture();
        Image image = ImageCache.getImage(texture.getSource());

        rectangle.setX(entityInstance.getPosX() - image.getWidth() / 2);
        rectangle.setY(entityInstance.getPosY() - image.getHeight() / 2);
        rectangle.setWidth(image.getWidth());
        rectangle.setHeight(image.getHeight());

        return rectangle;
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
