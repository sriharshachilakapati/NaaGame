package com.naagame.player.html;

import com.google.gwt.core.client.EntryPoint;
import com.shc.silenceengine.backend.gwt.GwtRuntime;
import com.naagame.player.NaaGamePlayer;

public class NaaGamePlayerLauncher implements EntryPoint
{
    @Override
    public void onModuleLoad()
    {
        GwtRuntime.start(new NaaGamePlayer());
    }
}