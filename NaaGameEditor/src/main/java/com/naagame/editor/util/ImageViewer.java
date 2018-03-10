package com.naagame.editor.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class ImageViewer extends BorderPane {

    @FXML private Slider zoomSlider;
    @FXML private ImageView imageView;

    public ImageViewer() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("imageViewer.fxml"));

        try {
            loader.setRoot(this);
            loader.setController(this);

            loader.load();
            init();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void init() {
        imageProperty().addListener((o, oldImage, newImage) -> updateViewer());
        zoomProperty().addListener((o, oldZoom, newZoom) -> updateViewer());
    }

    private void updateViewer() {
        Image image = imageProperty().get();

        if (image == null) {
            return;
        }

        double zoom = zoomProperty().get();

        double width = image.getWidth();
        double height = image.getHeight();

        zoom = zoom / 100.0;

        width = Math.max(1, width * zoom);
        height = Math.max(1, height * zoom);

        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    public void setImage(Image image) {
        imageProperty().set(image);
    }

    public void setZoom(double zoom) {
        zoomProperty().set(zoom);
    }

    private ObjectProperty<Image> imageProperty() {
        return imageView.imageProperty();
    }

    private DoubleProperty zoomProperty() {
        return zoomSlider.valueProperty();
    }
}
