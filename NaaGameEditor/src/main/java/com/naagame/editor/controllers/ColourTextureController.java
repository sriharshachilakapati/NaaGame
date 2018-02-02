package com.naagame.editor.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ColourTextureController implements Initializable {
    private @FXML ColorPicker colourPicker;
    private @FXML Spinner<Integer> widthSpinner;
    private @FXML Spinner<Integer> heightSpinner;

    public Color getColour() {
        return colourPicker.getValue();
    }

    public int getWidth() {
        return widthSpinner.getValue();
    }

    public int getHeight() {
        return heightSpinner.getValue();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 512, 16));
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 512, 16));
    }
}
