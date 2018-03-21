package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.util.ImageCache;
import com.naagame.editor.util.ImageViewer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BackgroundEditorController extends Controller implements Initializable {
    @FXML private Slider hSpeedSlider;
    @FXML private Slider vSpeedSlider;
    @FXML private TitledPane previewPane;
    @FXML private ChoiceBox<String> textureSelector;

    private NgmBackground background;
    private ImageViewer imageViewer;

    private boolean changed;
    private NgmTexture texture;

    private float hSpeed;
    private float vSpeed;

    @Override
    public void init(String name) {
        background = NgmProject.find(NgmProject.backgrounds, name);
        hSpeed = background.getHSpeed();
        vSpeed = background.getVSpeed();
        texture = background.getTexture();

        if (texture != null) {
            textureSelector.getSelectionModel().select(texture.getName());
        }

        hSpeedSlider.setValue(hSpeed);
        vSpeedSlider.setValue(vSpeed);

        resourcesChanged();

        changed = false;
    }

    @FXML
    @Override
    public void commitChanges() {
        changed = false;

        background.setHSpeed(hSpeed);
        background.setVSpeed(vSpeed);
        background.setTexture(texture);

        notifySave();
    }

    @FXML
    @Override
    public void discardChanges() {
        init(background.getName());
    }

    @Override
    public boolean hasUnsavedEdits() {
        return changed;
    }

    @Override
    public void resourcesChanged() {
        textureSelector.getItems().clear();
        textureSelector.getItems().addAll(NgmProject.textures.stream()
                .map(NgmTexture::getName)
                .collect(Collectors.toList()));

        if (texture == null) {
            return;
        }

        texture = NgmProject.find(NgmProject.textures, texture.getName());

        if (texture == null) {
            textureSelector.getSelectionModel().clearSelection();
            imageViewer.setImage(null);
        } else {
            textureSelector.getSelectionModel().select(texture.getName());
            imageViewer.setImage(ImageCache.getImage(texture.getSource()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageViewer = new ImageViewer();
        previewPane.setContent(imageViewer);

        textureSelector.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            if (n == null) {
                imageViewer.setImage(null);
            } else {
                NgmTexture newTexture = NgmProject.find(NgmProject.textures, n);
                imageViewer.setImage(ImageCache.getImage(newTexture.getSource()));
                changed = texture != newTexture;
                texture = newTexture;
            }
        });

        hSpeedSlider.valueProperty().addListener((v, o, n) -> {
            hSpeed = n.floatValue();
            changed = true;
        });

        vSpeedSlider.valueProperty().addListener((v, o, n) -> {
            vSpeed = n.floatValue();
            changed = true;
        });
    }
}
