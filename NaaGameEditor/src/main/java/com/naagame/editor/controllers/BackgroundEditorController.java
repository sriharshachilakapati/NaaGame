package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.util.ImageUtil;
import com.naagame.editor.util.ImageViewer;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;

import java.util.stream.Collectors;

public class BackgroundEditorController implements IController {
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
        if (!changed) {
            imageViewer = new ImageViewer();
            previewPane.setContent(imageViewer);

            textureSelector.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
                texture = NgmProject.find(NgmProject.textures, n);
                imageViewer.setImage(ImageUtil.loadImage(texture.getSource()));
                changed = true;
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
        if (texture != null) {
            imageViewer.setImage(ImageUtil.loadImage(texture.getSource()));
        }

        textureSelector.getItems().clear();
        textureSelector.getItems().addAll(NgmProject.textures.stream()
                .map(NgmTexture::getName)
                .collect(Collectors.toList()));
    }
}
