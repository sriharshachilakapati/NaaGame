package com.naagame.editor.util;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.*;
import com.naagame.editor.controllers.SceneEditorController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class LevelEditor {
    private final GraphicsContext ctx;
    private final Canvas canvas;
    private final SceneEditorController controller;

    private double mouseX;
    private double mouseY;

    public LevelEditor(SceneEditorController controller, Canvas canvas) {
        this.canvas = canvas;
        this.controller = controller;
        this.ctx = canvas.getGraphicsContext2D();

        canvas.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();

            redraw();
        });
    }

    public void redraw() {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (NgmScene.Instance<NgmBackground> bgInstance : controller.getBackgrounds()) {
            NgmBackground background = bgInstance.getObject();
            NgmTexture bgTexture = background.getTexture();

            if (bgTexture == null) {
                continue;
            }

            Image textureImage = ImageCache.getImage(bgTexture.getSource());

            if (textureImage != null) {
                ctx.drawImage(textureImage, bgInstance.getPosX(), bgInstance.getPosY(),
                        canvas.getWidth(), canvas.getHeight());
            }
        }

        for (NgmScene.Instance<NgmEntity> entityInstance : controller.getEntities()) {
            NgmEntity entity = entityInstance.getObject();
            NgmSprite sprite = entity.getSprite();

            if (sprite == null || sprite.getFrames().size() == 0) {
                continue;
            }

            NgmTexture texture = sprite.getFrames().get(0).getTexture();

            if (texture == null) {
                continue;
            }

            Image textureImage = ImageCache.getImage(texture.getSource());

            if (textureImage != null) {
                ctx.drawImage(textureImage,
                        entityInstance.getPosX() - textureImage.getWidth() / 2,
                        entityInstance.getPosY() - textureImage.getHeight() / 2);
            }
        }

        String selected;

        if ((selected = controller.getSelectedEntity()) != null) {
            NgmEntity entity = NgmProject.find(NgmProject.entities, selected);

            NgmSprite sprite = entity.getSprite();

            if (sprite == null || sprite.getFrames().size() == 0) {
                return;
            }

            NgmTexture texture = sprite.getFrames().get(0).getTexture();

            if (texture == null) {
                return;
            }

            Image image = ImageCache.getImage(texture.getSource());

            if (image == null) {
                return;
            }

            ctx.setGlobalAlpha(0.75);
            ctx.drawImage(image, mouseX - image.getWidth() / 2, mouseY - image.getHeight() / 2);
            ctx.setGlobalAlpha(1.0);
        }
    }
}
