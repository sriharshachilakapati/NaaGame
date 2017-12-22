package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SpriteEditorController {
    String currentSprite;

    @FXML TextField nameField;

    public void init() {
        nameField.setText(currentSprite);
    }
}
