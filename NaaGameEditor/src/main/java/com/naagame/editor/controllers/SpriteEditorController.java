package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.IResource;
import com.naagame.core.resources.NgmSprite;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.util.ImageCache;
import com.naagame.editor.util.ImageViewer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SpriteEditorController implements IController, Initializable {
    private @FXML TableView<NgmSprite.Frame> framesView;
    private @FXML TableColumn<NgmSprite.Frame, String> textureColumn;
    private @FXML TableColumn<NgmSprite.Frame, Integer> durationColumn;
    private @FXML TitledPane previewPane;

    private ObservableList<String> textures;
    private ObservableList<NgmSprite.Frame> frames;
    private NgmSprite currentSprite;
    private ImageViewer imageViewer;

    private Timeline timeline;

    @Override
    public void init(String name) {
        currentSprite = NgmProject.find(NgmProject.sprites, name);

        frames.clear();
        frames.addAll(currentSprite.getFrames());

        resourcesChanged();
        updateAnimation();
    }

    @FXML
    public void onAddFrameButtonClicked() {
        if (NgmProject.textures.size() == 0) {
            return;
        }

        frames.add(new NgmSprite.Frame(NgmProject.textures.get(0), 250));
        Platform.runLater(this::updateAnimation);
    }

    @FXML
    public void onRemoveFrameButtonClicked() {
        NgmSprite.Frame selected = framesView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        frames.remove(selected);
        Platform.runLater(this::updateAnimation);
    }

    private void updateAnimation() {
        timeline.stop();

        AtomicInteger frame = new AtomicInteger(0);
        timeline.getKeyFrames().clear();

        timeline.getKeyFrames().addAll(frames.stream().map(f -> {
            Image image = ImageCache.getImage(f.getTexture().getSource());
            Duration duration = Duration.millis(f.getDuration() * frame.addAndGet(1));

            return new KeyFrame(duration, e -> imageViewer.setImage(image));
        }).collect(Collectors.toList()));

        timeline.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageViewer = new ImageViewer();
        previewPane.setContent(imageViewer);

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        textures = FXCollections.observableArrayList();
        frames = FXCollections.observableArrayList();

        textureColumn.setCellValueFactory(param -> {
            NgmSprite.Frame frame = param.getValue();
            NgmTexture texture = frame.getTexture();
            return new SimpleObjectProperty<>(texture == null ? "" : texture.getName());
        });
        textureColumn.setCellFactory(ComboBoxTableCell.forTableColumn(textures));
        textureColumn.setOnEditCommit(event -> {
            event.getRowValue()
                    .setTexture(NgmProject.find(NgmProject.textures, event.getNewValue()));
            Platform.runLater(this::updateAnimation);
        });

        durationColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDuration()));
        durationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer i) {
                return String.valueOf(i);
            }

            @Override
            public Integer fromString(String s) {
                return Integer.valueOf(s);
            }
        }));

        durationColumn.setOnEditCommit(event -> {
            event.getRowValue().setDuration(event.getNewValue());
            Platform.runLater(this::updateAnimation);
        });

        framesView.setItems(frames);
    }

    @Override
    public void resourcesChanged() {
        textures.clear();
        textures.addAll(NgmProject.textures
                .stream().map(IResource::getName).collect(Collectors.toList()));

        Platform.runLater(this::updateAnimation);
    }
}
