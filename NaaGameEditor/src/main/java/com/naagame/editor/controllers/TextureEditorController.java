package com.naagame.editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TextureEditorController implements IController {
    @FXML public TextField nameField;

    @Override
    public void init(String currentTexture) {
        nameField.setText(currentTexture);
    }
}
