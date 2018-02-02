package com.naagame.editor.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageUtil {
    public static Image loadImage(String source) {
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

                return image;
            } else {
                try {
                    return new Image(Files.newInputStream(Paths.get(source)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
