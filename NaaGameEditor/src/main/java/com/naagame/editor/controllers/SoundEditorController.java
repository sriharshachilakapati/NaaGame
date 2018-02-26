package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.NgmSound;
import com.naagame.editor.util.PathResolver;
import com.naagame.editor.util.RetentionFileChooser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Path;

public class SoundEditorController extends Controller {
    private NgmSound currentSound;

    private String source;
    private MediaPlayer player;

    @FXML private Button playButton;
    @FXML private Button stopButton;

    private boolean changed;

    @FXML private Slider progressSlider;
    @FXML private CheckBox loopCheckBox;
    @FXML private CheckBox playOnStartCheckBox;

    @Override
    public void init(String name) {
        currentSound = NgmProject.find(NgmProject.sounds, name);

        source = currentSound.getSource();
        loopCheckBox.setSelected(currentSound.isLoop());
        playOnStartCheckBox.setSelected(currentSound.isPlayOnStart());

        playButton.setDisable(true);
        stopButton.setDisable(true);

        if (!source.equals("")) {
            createMediaPlayer(PathResolver.resolve(source));
        } else {
            player = null;
        }

        changed = false;
    }

    @FXML
    private void onLoadFromFileClicked() {
        Path path = RetentionFileChooser.showOpenDialog(RetentionFileChooser.EXTENSION_FILTER_SOUNDS);

        if (path == null) {
            return;
        }

        changed = true;

        source = path.toString();
        createMediaPlayer(path);
    }

    private void createMediaPlayer(Path path) {
        Media media = new Media(path.toUri().toString());
        player = new MediaPlayer(media);

        player.currentTimeProperty().addListener((ov, o, n) -> {
            double current = n.toMillis();
            double total = player.getTotalDuration().toMillis();

            double progress = current / total * 100;
            progressSlider.setValue(progress);
        });

        player.setOnPlaying(() -> {
            stopButton.setDisable(false);
            playButton.setDisable(true);
        });

        player.setOnEndOfMedia(() -> {
            progressSlider.setValue(0);
            player.stop();
            stopButton.setDisable(true);
            playButton.setDisable(false);
        });

        playButton.setDisable(false);
    }

    @FXML
    private void onPlayClicked() {
        player.play();
    }

    @FXML
    private void onStopClicked() {
        player.stop();
    }

    @Override
    protected boolean hasUnsavedEdits() {
        return changed;
    }

    @FXML
    @Override
    protected void commitChanges() {
        currentSound.setSource(source);
        currentSound.setPlayOnStart(playOnStartCheckBox.isSelected());
        currentSound.setLoop(loopCheckBox.isSelected());
        changed = false;
    }

    @FXML
    @Override
    protected void discardChanges() {
        init(currentSound.getName());
    }
}
