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

        NgmProject.textures.forEach(texture -> {
            if (!texture.getFileName().equals("")) {
                long id = loader.define(Texture.class, FilePath.getResourceFile(texture.getFileName()));
                textureIds.put(texture.getName(), id);
            }
        });

        NgmProject.sounds.forEach(sound -> {
            if (!sound.getFileName().equals("")) {
                long id = loader.define(Sound.class, FilePath.getResourceFile(sound.getFileName()));
                soundIds.put(sound.getName(), id);
            }
        });

        return new LoadingState(loader, () -> {
            NaaGamePlayer.logger.info("Fetching all the loaded textures");
            for (NgmTexture ngmTexture : NgmProject.textures) {
                if (!ngmTexture.getFileName().equals("")) {
                    long id = textureIds.get(ngmTexture.getName());
                    Resources.textures.put(ngmTexture.getName(), loader.get(id));
                } else {
                    NaaGamePlayer.logger.warn("Texture " + ngmTexture.getName() + " has no source. Ignoring it.");
                    Resources.textures.put(ngmTexture.getName(), Texture.EMPTY);
                }
            }

            NaaGamePlayer.logger.info("Fetching all the loaded sounds");
            for (NgmSound ngmSound : NgmProject.sounds) {
                if (!ngmSound.getFileName().equals("")) {
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
            NaaGamePlayer.instance.setGameState(new SceneState());
        });
    }
}
