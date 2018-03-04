package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSprite;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EntityEditorController extends Controller implements Initializable {

    @FXML private ChoiceBox<String> spriteSelector;

    private NgmEntity entity;
    private NgmSprite sprite;
    private boolean changed;

    @Override
    public void init(String name) {
        entity = NgmProject.find(NgmProject.entities, name);
        sprite = entity.getSprite();
        changed = false;

        if (sprite != null) {
            spriteSelector.getSelectionModel().select(sprite.getName());
        }

        resourcesChanged();
    }

    @FXML
    @Override
    protected void discardChanges() {
        init(entity.getName());
    }

    @Override
    protected void resourcesChanged() {
        spriteSelector.getItems().clear();
        spriteSelector.getItems().addAll(NgmProject.sprites.stream()
                .map(NgmSprite::getName)
                .collect(Collectors.toList()));

        if (sprite == null) {
            return;
        }

        sprite = NgmProject.find(NgmProject.sprites, sprite.getName());

        if (sprite == null) {
            spriteSelector.getSelectionModel().clearSelection();
        } else {
            spriteSelector.getSelectionModel().select(sprite.getName());
        }
    }

    @Override
    protected boolean hasUnsavedEdits() {
        return changed;
    }

    @FXML
    @Override
    protected void commitChanges() {
        entity.setSprite(sprite);
        changed = false;
        notifySave();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spriteSelector.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (n != null) {
                sprite = NgmProject.find(NgmProject.sprites, n);
                changed = true;
            }
        });
    }
}
