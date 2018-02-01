package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.util.RetentionFileChooser;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.naagame.editor.util.RetentionFileChooser.EXTENSION_FILTER_IMAGES;

public class TextureEditorController implements IController {
    @FXML public ImageView texturePreview;

    private NgmTexture currentTexture;

    @Override
    public void init(String resourceName) {
        currentTexture = NgmProject.find(NgmProject.textures, resourceName);
    }

    @FXML
    public void onFromFileButtonClicked() {
        Path path = RetentionFileChooser.showOpenDialog(null, EXTENSION_FILTER_IMAGES);

        if (path != null) {
            try {
                texturePreview.setImage(new Image(Files.newInputStream(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean hasUnsavedEdits() {
        return true;
    }

    @Override
    public void commitChanges() {
    }

    @Override
    public void discardChanges() {
    }
}
