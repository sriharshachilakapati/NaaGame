package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.Main;
import com.naagame.editor.util.ImageUtil;
import com.naagame.editor.util.ImageViewer;
import com.naagame.editor.util.RetentionFileChooser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.naagame.editor.util.RetentionFileChooser.EXTENSION_FILTER_IMAGES;

public class TextureEditorController implements IController {
    @FXML private TitledPane previewPane;

    private NgmTexture texture;
    private ImageViewer imageViewer;

    private boolean changed;
    private String source;

    @Override
    public void init(String resourceName) {
        if (!changed) {
            imageViewer = new ImageViewer();
            previewPane.setContent(imageViewer);
            changed = false;
        }

        texture = NgmProject.find(NgmProject.textures, resourceName);
        source = texture.getSource();

        updateImagePreview();
    }

    private void updateImagePreview() {
        imageViewer.setZoom(100);
        imageViewer.setImage(ImageUtil.loadImage(source));
    }

    @FXML
    public void onFromFileButtonClicked() {
        Path path = RetentionFileChooser.showOpenDialog(EXTENSION_FILTER_IMAGES);

        if (path != null) {
            changed = true;

            try {
                source = path.toAbsolutePath().toString();
                imageViewer.setImage(new Image(Files.newInputStream(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }

    @FXML
    @Override
    public void discardChanges() {
        init(texture.getName());
    }
}
