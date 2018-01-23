package com.naagame.editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BackgroundEditorController implements IController {
    @FXML TextField nameField;

    @Override
    public void init(String currentBackground) {
        nameField.setText(currentBackground);
    }
}
