package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TextureEditorController {
    String currentTexture;

    @FXML public TextField nameField;

    public void init() {
        nameField.setText(currentTexture);
    }
}
