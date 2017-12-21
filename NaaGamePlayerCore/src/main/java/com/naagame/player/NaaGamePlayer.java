package com.naagame.player;

import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.input.Keyboard;

public class NaaGamePlayer extends Game
{
    @Override
    public void init()
    {
        SilenceEngine.display.setTitle("NaaGamePlayer: SilenceEngine " + SilenceEngine.getVersionString());
        SilenceEngine.display.setSize(1280, 720);
        SilenceEngine.display.centerOnScreen();
    }

    @Override
    public void update(float deltaTime)
    {
        if (Keyboard.isKeyTapped(Keyboard.KEY_ESCAPE))
            SilenceEngine.display.close();
    }
}