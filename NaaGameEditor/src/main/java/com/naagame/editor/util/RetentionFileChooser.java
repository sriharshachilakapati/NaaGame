package com.naagame.editor.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;

public final class RetentionFileChooser {
    private static FileChooser instance;

    private static SimpleObjectProperty<File> lastDirProperty = new SimpleObjectProperty<>();

    public static final FileChooser.ExtensionFilter EXTENSION_FILTER_NAAGAME_PROJ =
            new FileChooser.ExtensionFilter("NaaGame Projects", "*.ngm");
    public static final FileChooser.ExtensionFilter EXTENSION_FILTER_IMAGES =
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.gif");
    public static final FileChooser.ExtensionFilter EXTENSION_FILTER_SOUNDS =
            new FileChooser.ExtensionFilter("Sound Files", "*.wav", "*.ogg", "*.oga");

    private RetentionFileChooser() {
    }

    private static FileChooser getInstance() {
        if (instance == null) {
            instance = new FileChooser();
            instance.initialDirectoryProperty().bindBidirectional(lastDirProperty);
        }

        return instance;
    }

    public static Path showOpenDialog(Window window, FileChooser.ExtensionFilter ... filters) {
        FileChooser fc = getInstance();
        fc.setTitle("Open file");
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().addAll(filters);

        File selected = fc.showOpenDialog(window);

        if (selected == null) {
            return null;
        }

        lastDirProperty.set(selected.getParentFile());
        return selected.toPath();
    }

    public static Path showSaveDialog(Window window) {
        FileChooser fc = getInstance();
        fc.setTitle("Save As");
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(EXTENSION_FILTER_NAAGAME_PROJ);

        File selected = fc.showSaveDialog(window);

        if (selected == null) {
            return null;
        }

        lastDirProperty.set(selected.getParentFile());
        return selected.toPath();
    }
}
