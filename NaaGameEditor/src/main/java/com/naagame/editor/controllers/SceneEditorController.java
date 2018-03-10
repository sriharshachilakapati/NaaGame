package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmScene;
import com.naagame.editor.util.LevelEditor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SceneEditorController extends Controller implements Initializable {

    @FXML private Canvas sceneCanvas;

    @FXML private Spinner<Integer> spinnerWidth;
    @FXML private Spinner<Integer> spinnerHeight;

    private LevelEditor levelEditor;

    @Override
    public void init(String name) {
        NgmScene scene = NgmProject.find(NgmProject.scenes, name);
        levelEditor = new LevelEditor(scene, sceneCanvas);
        levelEditor.redraw();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinnerHeight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50000, 600));
        spinnerWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50000, 800));

        spinnerWidth.valueProperty().addListener((ov, o, n) -> {
            sceneCanvas.setWidth(n);
            levelEditor.redraw();
        });
        spinnerHeight.valueProperty().addListener((ov, o, n) -> {
            sceneCanvas.setHeight(n);
            levelEditor.redraw();
        });

        spinnerWidth.focusedProperty().addListener((ov, o, n) -> spinnerWidth.increment(0));
        spinnerHeight.focusedProperty().addListener((ov, o, n) -> spinnerHeight.increment(0));
    }
}
