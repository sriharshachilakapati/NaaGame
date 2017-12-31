package com.naagame.editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TextureEditorController implements IController {
    @FXML public TextField nameField;

    String currentTexture;

    public void init() {
        nameField.setText(currentTexture);
    }
}
