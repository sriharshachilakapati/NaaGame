package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.*;
import com.naagame.editor.util.ImageCache;
import com.naagame.editor.util.LevelEditor;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

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
    @FXML private ToggleButton gridToggleButton;

    @FXML private Slider gridXSlider;
    @FXML private Slider gridYSlider;

    @FXML private TableView<NgmScene.Instance<NgmBackground>> backgroundsTable;
    @FXML private TableColumn<NgmScene.Instance<NgmBackground>, String> backgroundColumn;
    @FXML private TableColumn<NgmScene.Instance<NgmBackground>, Float> xPosColumn;
    @FXML private TableColumn<NgmScene.Instance<NgmBackground>, Float> yPosColumn;

    private ObservableList<String> allBackgrounds;
    private ObservableList<NgmScene.Instance<NgmBackground>> backgrounds;

    private LevelEditor levelEditor;

    private List<NgmScene.Instance<NgmEntity>> entities;

    private <T extends IResource> NgmScene.Instance<T> cloneInstance(NgmScene.Instance<T> instance) {
        return new NgmScene.Instance<>(instance.getObject(), instance.getPosX(), instance.getPosY());
    }

    @Override
    public void init(String name) {
        NgmScene scene = NgmProject.find(NgmProject.scenes, name);

        backgrounds.addAll(scene.getBackgrounds().stream().map(this::cloneInstance).collect(Collectors.toList()));

        entities = new ArrayList<>();
        entities.addAll(scene.getEntities().stream().map(this::cloneInstance).collect(Collectors.toList()));

        levelEditor = new LevelEditor(this, sceneCanvas);
        levelEditor.redraw();

        resourcesChanged();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backgrounds = FXCollections.observableArrayList();
        allBackgrounds = FXCollections.observableArrayList();

        backgroundColumn.setCellValueFactory(param -> {
            NgmScene.Instance<NgmBackground> bgInstance = param.getValue();
            NgmBackground background = bgInstance.getObject();
            return new SimpleObjectProperty<>(background == null ? "" : background.getName());
        });
        backgroundColumn.setCellFactory(ComboBoxTableCell.forTableColumn(allBackgrounds));
        backgroundColumn.setOnEditCommit(event -> {
            event.getRowValue().setObject(NgmProject.find(NgmProject.backgrounds, event.getNewValue()));
            levelEditor.redraw();
        });

        StringConverter<Float> converter = new StringConverter<Float>() {
            @Override
            public String toString(Float object) {
                return String.valueOf(object);
            }

            @Override
            public Float fromString(String string) {
                return Float.parseFloat(string);
            }
        };

        xPosColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPosX()));
        xPosColumn.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        xPosColumn.setOnEditCommit(event -> {
            event.getRowValue().setPosX(event.getNewValue());
            levelEditor.redraw();
        });

        yPosColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPosY()));
        yPosColumn.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        yPosColumn.setOnEditCommit(event -> {
            event.getRowValue().setPosY(event.getNewValue());
            levelEditor.redraw();
        });

        backgroundsTable.setItems(backgrounds);

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

        gridToggleButton.setOnAction(e -> levelEditor.redraw());
        gridXSlider.valueProperty().addListener((ov, o, n) -> levelEditor.redraw());
        gridYSlider.valueProperty().addListener((ov, o, n) -> levelEditor.redraw());

        sceneCanvas.setOnMouseClicked(event -> {
            String selected = getSelectedEntity();

            if (selected == null) {
                return;
            }

            switch (event.getButton()) {
                case PRIMARY:
                    entities.add(new NgmScene.Instance<>(NgmProject.find(NgmProject.entities, selected),
                            levelEditor.getCorrectedMouseX(),
                            levelEditor.getCorrectedMouseY()));
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

        allBackgrounds.clear();
        allBackgrounds.addAll(NgmProject.backgrounds.stream().map(IResource::getName).collect(Collectors.toList()));

        backgrounds.removeIf(bgInstance ->
                NgmProject.find(NgmProject.backgrounds, bgInstance.getObject().getName()) == null);
    }

    @FXML
    private void onAddBackgroundClick() {
        if (NgmProject.backgrounds.size() == 0) {
            return;
        }

        backgrounds.add(new NgmScene.Instance<>(NgmProject.backgrounds.get(0), 0, 0));
        levelEditor.redraw();
    }

    @FXML
    private void onDeleteBackgroundClick() {
        NgmScene.Instance<NgmBackground> selected = backgroundsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        backgrounds.remove(selected);
        levelEditor.redraw();
    }

    public String getSelectedEntity() {
        return entitySelector.getSelectionModel().getSelectedItem();
    }

    public boolean isGridEnabled() {
        return gridToggleButton.isSelected();
    }

    public double getGridX() {
        return gridXSlider.getValue();
    }

    public double getGridY() {
        return gridYSlider.getValue();
    }

    public List<NgmScene.Instance<NgmBackground>> getBackgrounds() {
        return backgrounds;
    }

    public List<NgmScene.Instance<NgmEntity>> getEntities() {
        return entities;
    }
}
