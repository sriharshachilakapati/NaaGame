package com.naagame.player.desktop;

import com.shc.silenceengine.backend.lwjgl.LwjglRuntime;
import com.naagame.player.NaaGamePlayer;
import com.shc.silenceengine.io.FilePath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NaaGamePlayerLauncher
{
    public static void main(String[] args)
    {
        NaaGamePlayer.filePathProvider = FilePath::getResourceFile;

        if (args.length == 1) {
            String path = args[0];
            String parent = Paths.get(path).toAbsolutePath().getParent().toAbsolutePath().toString();

            path = Paths.get(parent).relativize(Paths.get(path)).toString();

            NaaGamePlayer.filePathProvider = name -> {
                Path newPath = Paths.get(parent).resolve(name);
                return FilePath.getExternalFile(newPath.toString());
            };

            LwjglRuntime.start(new NaaGamePlayer(path));
        } else {
            LwjglRuntime.start(new NaaGamePlayer());
        }
    }
}