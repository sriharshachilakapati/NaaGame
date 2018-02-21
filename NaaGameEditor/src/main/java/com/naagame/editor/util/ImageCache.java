package com.naagame.editor.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static Map<String, Image> imageCache = new HashMap<>();

    private static Path projectDir;

    public static void setProjectDir(Path projectDir) {
        ImageCache.projectDir = projectDir;
    }

    public static Image updateCache(String source) {
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

                imageCache.put(source, image);
                return image;
            } else {
                try {
                    Path path;

                    if (source.startsWith("resources")) {
                        path = projectDir.resolve(source);
                    } else {
                        path = Paths.get(source);
                    }

                    Image image = new Image(Files.newInputStream(path));
                    imageCache.put(source, image);

                    return image;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static Image getImage(String source) {
        if (imageCache.containsKey(source)) {
            return imageCache.get(source);
        }

        return updateCache(source);
    }
}
