package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmEntity;
import com.naagame.core.resources.NgmSound;
import com.naagame.core.resources.NgmSprite;
import com.naagame.core.resources.NgmTexture;
import com.shc.silenceengine.audio.Sound;
import com.shc.silenceengine.audio.openal.ALBuffer;
import com.shc.silenceengine.collision.CollisionTag;
import com.shc.silenceengine.core.ResourceLoader;
import com.shc.silenceengine.graphics.Animation;
import com.shc.silenceengine.graphics.Color;
import com.shc.silenceengine.graphics.opengl.Texture;
import com.shc.silenceengine.io.FilePath;
import com.shc.silenceengine.utils.ResourceLoadingState;
import com.shc.silenceengine.utils.TimeUtils;
import com.shc.silenceengine.utils.functional.SimpleCallback;

import java.util.HashMap;
import java.util.Map;

public class LoadingState extends ResourceLoadingState {
    private LoadingState(ResourceLoader loader, SimpleCallback doneCallback) {
        super(loader, doneCallback);
    }

    public static LoadingState create() {
        ResourceLoader loader = new ResourceLoader();

        Map<String, Long> textureIds = new HashMap<>();
        Map<String, Long> soundIds = new HashMap<>();

        loader.define(Texture.class, FilePath.getResourceFile("engine_resources/logo.png"));

        NgmProject.textures.forEach(texture -> {
            final String source = texture.getSource();

            if (!source.equals("") && !source.startsWith("colour:")) {
                long id = loader.define(Texture.class, FilePath.getResourceFile(source));
                textureIds.put(texture.getName(), id);
            }
        });

        NgmProject.sounds.forEach(sound -> {
            if (!sound.getSource().equals("")) {
                long id = loader.define(Sound.class, FilePath.getResourceFile(sound.getSource()));
                soundIds.put(sound.getName(), id);
            }
        });

        return new LoadingState(loader, () -> {
            NaaGamePlayer.logger.info("Fetching all the loaded textures");
            for (NgmTexture ngmTexture : NgmProject.textures) {
                final String source = ngmTexture.getSource();

                if (!source.equals("")) {
                    if (source.startsWith("colour:")) {
                        String[] parts = source.replaceAll("colour:", "").split(";");

                        float r = Float.parseFloat(parts[0]);
                        float g = Float.parseFloat(parts[1]);
                        float b = Float.parseFloat(parts[2]);
                        float a = Float.parseFloat(parts[3]);

                        int w = Integer.parseInt(parts[4]);
                        int h = Integer.parseInt(parts[5]);

                        Resources.textures.put(ngmTexture.getName(), Texture.fromColor(new Color(r, g, b, a), w, h));
                    } else {
                        long id = textureIds.get(ngmTexture.getName());
                        Resources.textures.put(ngmTexture.getName(), loader.get(id));
                    }
                } else {
                    NaaGamePlayer.logger.warn("Texture " + ngmTexture.getName() + " has no source. Ignoring it.");
                    Resources.textures.put(ngmTexture.getName(), Texture.EMPTY);
                }
            }

            NaaGamePlayer.logger.info("Fetching all the loaded sounds");
            for (NgmSound ngmSound : NgmProject.sounds) {
                if (!ngmSound.getSource().equals("")) {
                    long id = soundIds.get(ngmSound.getName());
                    Resources.sounds.put(ngmSound.getName(), loader.get(id));
                } else {
                    NaaGamePlayer.logger.warn("Sound " + ngmSound.getName() + " has no source. Ignoring it.");
                    Resources.sounds.put(ngmSound.getName(), new Sound(new ALBuffer()));
                }
            }

            NaaGamePlayer.logger.info("Creating animations for sprites");
            for (NgmSprite ngmSprite : NgmProject.sprites) {
                Animation animation = new Animation();

                ngmSprite.getFrames().forEach(frame -> {
                    NgmTexture ngmTexture = frame.getTexture();
                    Texture texture = Resources.textures.get(ngmTexture.getName());
                    int duration = frame.getDuration();
                    animation.addFrame(texture, duration, TimeUtils.Unit.MILLIS);
                });

                Resources.animations.put(ngmSprite.getName(), animation);
            }

            NaaGamePlayer.logger.info("Creating collision tags for all entities");
            for (NgmEntity ngmEntity : NgmProject.entities) {
                Resources.collisionTags.put(ngmEntity.getName(), new CollisionTag());
            }

            NaaGamePlayer.logger.info("Done loading all the resources");

            for (NgmSound ngmSound : NgmProject.sounds) {
                if (ngmSound.isPlayOnStart() && Resources.sounds.get(ngmSound.getName()) != null) {
                    Resources.sounds.get(ngmSound.getName()).play(ngmSound.isLoop());
                }
            }

            NaaGamePlayer.instance.setGameState(new SceneState(NgmProject.scenes.stream()
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    .getName()));
        });
    }
}
