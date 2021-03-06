package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.Main;
import com.naagame.editor.util.ImageCache;
import com.naagame.editor.util.ImageViewer;
import com.naagame.editor.util.RetentionFileChooser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

import static com.naagame.editor.util.RetentionFileChooser.EXTENSION_FILTER_IMAGES;

public class TextureEditorController extends Controller implements Initializable {
    @FXML private TitledPane previewPane;

    private NgmTexture texture;
    private ImageViewer imageViewer;

    private boolean changed;
    private String source;

    @Override
    public void init(String resourceName) {
        texture = NgmProject.find(NgmProject.textures, resourceName);
        source = texture.getSource();

        updateImagePreview();
    }

    private void updateImagePreview() {
        imageViewer.setZoom(100);
        imageViewer.setImage(ImageCache.getImage(source));
    }

    @FXML
    public void onFromFileButtonClicked() {
        Path path = RetentionFileChooser.showOpenDialog(EXTENSION_FILTER_IMAGES);

        if (path != null) {
            changed = true;

            source = path.toAbsolutePath().toString();
            ImageCache.updateCache(source);
            updateImagePreview();
        }
    }

    @FXML
    public void onFromColourButtonClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("colourTexture.fxml"));
        try {
            DialogPane content = loader.load();
            ColourTextureController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(Main.window);

            dialog.setTitle("NaaGame");
            dialog.setHeaderText("Create Texture");
            dialog.setDialogPane(content);

            ButtonType saveBtn = new ButtonType("Save");
            ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            content.getButtonTypes().clear();
            content.getButtonTypes().addAll(saveBtn, cancelBtn);

            dialog.setResultConverter(x -> x);

            ButtonType selected = dialog.showAndWait().orElse(cancelBtn);

            if (selected == saveBtn) {
                changed = true;

                Color color = controller.getColour();

                int w = controller.getWidth();
                int h = controller.getHeight();

                float r = (float) color.getRed();
                float g = (float) color.getGreen();
                float b = (float) color.getBlue();
                float a = (float) color.getOpacity();

                source = String.format("colour:%f;%f;%f;%f;%d;%d;", r, g, b, a, w, h);
                updateImagePreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasUnsavedEdits() {
        return changed;
    }

    @FXML
    @Override
    public void commitChanges() {
        changed = false;
        texture.setSource(source);
        notifySave();
    }

    @FXML
    @Override
    public void discardChanges() {
        init(texture.getName());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageViewer = new ImageViewer();
        previewPane.setContent(imageViewer);
        changed = false;
    }
}
