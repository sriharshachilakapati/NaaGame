package com.naagame.player;

import com.naagame.core.NgmProject;
import com.naagame.core.io.ProjectReader;
import com.shc.easyjson.ParseException;
import com.shc.silenceengine.core.Game;
import com.shc.silenceengine.core.SilenceEngine;
import com.shc.silenceengine.input.Keyboard;
import com.shc.silenceengine.io.FilePath;
import com.shc.silenceengine.logging.Logger;

public class NaaGamePlayer extends Game {
    public static Logger logger;
    public static Game instance;

    @Override
    public void init() {
        instance = this;
        logger = SilenceEngine.log.getLogger("NaaGamePlayer");

        SilenceEngine.display.setTitle("NaaGamePlayer: SilenceEngine " + SilenceEngine.getVersionString());
        SilenceEngine.display.centerOnScreen();

        SilenceEngine.io.getFileReader()
                .readTextFile(FilePath.getResourceFile("test.ngm"))
                .then(this::projectLoaded);
    }

    private void projectLoaded(String json) {
        try {
            ProjectReader.loadFromJSON(json);
            logger.info("Fetched the project file, loading project resources");
            setGameState(LoadingState.create());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(float deltaTime) {
        if (Keyboard.isKeyTapped(Keyboard.KEY_ESCAPE))
            SilenceEngine.display.close();
    }
}