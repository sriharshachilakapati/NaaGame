package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BackgroundEditorController {
    String currentBackground;

    @FXML TextField nameField;

    public void init() {
        nameField.setText(currentBackground);
    }
}
