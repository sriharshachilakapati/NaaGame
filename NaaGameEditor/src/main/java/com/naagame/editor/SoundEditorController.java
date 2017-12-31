package com.naagame.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SoundEditorController implements IController {
    String currentSound;

    @FXML TextField nameField;

    public void init() {
        nameField.setText(currentSound);
    }
}
