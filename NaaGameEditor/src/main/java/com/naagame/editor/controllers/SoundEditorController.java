package com.naagame.editor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SoundEditorController implements IController {
    @FXML TextField nameField;

    @Override
    public void init(String currentSound) {
        nameField.setText(currentSound);
    }
}
