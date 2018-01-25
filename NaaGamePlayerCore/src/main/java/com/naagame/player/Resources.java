package com.naagame.player;

import com.shc.silenceengine.audio.Sound;
import com.shc.silenceengine.collision.CollisionTag;
import com.shc.silenceengine.graphics.Animation;
import com.shc.silenceengine.graphics.opengl.Texture;

import java.util.HashMap;
import java.util.Map;

public class Resources {
    public static final Map<String, Texture> textures = new HashMap<>();
    public static final Map<String, Sound> sounds = new HashMap<>();
    public static final Map<String, Animation> animations = new HashMap<>();
    public static final Map<String, CollisionTag> collisionTags = new HashMap<>();
}
