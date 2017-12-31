package com.naagame.editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BackgroundEditorController implements IController {
    String currentBackground;

    @FXML TextField nameField;

    public void init() {
        nameField.setText(currentBackground);
    }
}
