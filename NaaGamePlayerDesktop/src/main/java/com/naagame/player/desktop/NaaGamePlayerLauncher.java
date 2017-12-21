package com.naagame.player.desktop;

import com.shc.silenceengine.backend.lwjgl.LwjglRuntime;
import com.naagame.player.NaaGamePlayer;

public class NaaGamePlayerLauncher
{
    public static void main(String[] args)
    {
        LwjglRuntime.start(new NaaGamePlayer());
    }
}