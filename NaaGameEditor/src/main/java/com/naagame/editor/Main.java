package com.naagame.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("main.fxml")));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("NaaGame");
        stage.show();
    }
}
