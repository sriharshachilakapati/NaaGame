package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmTexture;
import com.naagame.editor.util.RetentionFileChooser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.naagame.editor.util.RetentionFileChooser.EXTENSION_FILTER_IMAGES;

public class TextureEditorController implements IController {
    @FXML public ImageView texturePreview;

    private NgmTexture texture;

    private boolean changed;
    private String source;

    @Override
    public void init(String resourceName) {
        texture = NgmProject.find(NgmProject.textures, resourceName);
        changed = false;
        source = texture.getSource();

        updateImagePreview();
    }

    private void updateImagePreview() {
        texturePreview.setImage(null);

        if (!"".equals(source)) {
            if (source.startsWith("colour:")) {
                String[] parts = source.replaceAll("colour:", "").split(";");

                float r = Float.parseFloat(parts[0]);
                float g = Float.parseFloat(parts[1]);
                float b = Float.parseFloat(parts[2]);
                float a = Float.parseFloat(parts[3]);

                int w = Integer.parseInt(parts[4]);
                int h = Integer.parseInt(parts[5]);

                WritableImage image = new WritableImage(w, h);
                Color color = new Color(r, g, b, a);

                PixelWriter writer = image.getPixelWriter();

                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        writer.setColor(i, j, color);
                    }
                }

                texturePreview.setImage(image);
            } else {
                try {
                    texturePreview.setImage(new Image(Files.newInputStream(Paths.get(source))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onFromFileButtonClicked() {
        Path path = RetentionFileChooser.showOpenDialog(null, EXTENSION_FILTER_IMAGES);

        if (path != null) {
            changed = true;

            try {
                source = path.toAbsolutePath().toString();
                texturePreview.setImage(new Image(Files.newInputStream(path)));
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
