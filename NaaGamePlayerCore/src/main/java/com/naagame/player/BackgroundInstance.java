package com.naagame.player;

import com.naagame.core.resources.NgmBackground;
import com.naagame.core.resources.NgmScene;
import com.shc.silenceengine.graphics.DynamicRenderer;
import com.shc.silenceengine.graphics.IGraphicsDevice;
import com.shc.silenceengine.graphics.opengl.Primitive;
import com.shc.silenceengine.graphics.opengl.Texture;

public class BackgroundInstance {
    private float x;
    private float y;

    private NgmBackground background;

    BackgroundInstance(NgmScene.Instance<NgmBackground> instance) {
        this.background = instance.getObject();

        this.x = instance.getPosX();
        this.y = instance.getPosY();
    }

    void update() {
        x += background.getHSpeed();
        y += background.getVSpeed();
    }

    void render() {
        DynamicRenderer renderer = IGraphicsDevice.Renderers.dynamic;

        if (background.getTexture() == null) {
            return;
        }

        Texture texture = Resources.textures.get(background.getTexture().getName());

        float xOffset = -x / texture.getWidth();
        float yOffset = -y / texture.getHeight();

        texture.bind();
        renderer.begin(Primitive.TRIANGLE_FAN);
        {
            renderer.vertex(0, 0);
            renderer.texCoord(xOffset, yOffset);
            renderer.vertex(1, 0);
            renderer.texCoord(1 + xOffset, yOffset);
            renderer.vertex(1, 1);
            renderer.texCoord(1 + xOffset, 1 + yOffset);
            renderer.vertex(0, 1);
            renderer.texCoord(xOffset, 1 + yOffset);
        }
        renderer.end();
    }
}
