package com.naagame.player.html;

import com.google.gwt.core.client.EntryPoint;
import com.shc.silenceengine.backend.gwt.GwtRuntime;
import com.naagame.player.NaaGamePlayer;
import com.shc.silenceengine.io.FilePath;

public class NaaGamePlayerLauncher implements EntryPoint
{
    @Override
    public void onModuleLoad()
    {
        NaaGamePlayer.filePathProvider = FilePath::getResourceFile;
        GwtRuntime.start(new NaaGamePlayer());
    }
}