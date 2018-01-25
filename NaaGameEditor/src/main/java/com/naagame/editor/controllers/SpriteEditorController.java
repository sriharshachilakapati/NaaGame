package com.naagame.editor.controllers;

import com.naagame.core.NgmProject;
import com.naagame.core.resources.IResource;
import com.naagame.core.resources.NgmSprite;
import com.naagame.core.resources.NgmTexture;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SpriteEditorController implements IController, Initializable {
    @FXML TextField nameField;

    @FXML TableView<NgmSprite.Frame> framesView;
    @FXML TableColumn<NgmSprite.Frame, String> textureColumn;
    @FXML TableColumn<NgmSprite.Frame, Integer> durationColumn;

    private ObservableList<String> textures;
    private ObservableList<NgmSprite.Frame> frames;
    private NgmSprite currentSprite;

    @Override
    public void init(String name) {
        currentSprite = NgmProject.find(NgmProject.sprites, name);
        nameField.setText(name);

        textures.clear();
        textures.addAll(NgmProject.textures
                .stream().map(IResource::getName).collect(Collectors.toList()));

        frames.clear();
        frames.addAll(currentSprite.getFrames());
    }

    @FXML
    public void onAddFrameButtonClicked() {
        if (NgmProject.textures.size() == 0) {
            return;
        }

        frames.add(new NgmSprite.Frame(NgmProject.textures.get(0), 250));
    }

    @FXML
    public void onRemoveFrameButtonClicked() {
        NgmSprite.Frame selected = framesView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        frames.remove(selected);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textures = FXCollections.observableArrayList();
        frames = FXCollections.observableArrayList();

        textureColumn.setCellValueFactory(param -> {
            NgmSprite.Frame frame = param.getValue();
            NgmTexture texture = frame.getTexture();
            return new SimpleObjectProperty<>(texture == null ? "" : texture.getName());
        });
        textureColumn.setCellFactory(ComboBoxTableCell.forTableColumn(textures));
        textureColumn.setOnEditCommit(event -> event.getRowValue()
                .setTexture(NgmProject.find(NgmProject.textures, event.getNewValue())));

        durationColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getDuration()));
        durationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer i) {
                return String.valueOf(i);
            }

            @Override
            public Integer fromString(String s) {
                return Integer.valueOf(s);
            }
        }));

        durationColumn.setOnEditCommit(event -> event.getRowValue().setDuration(event.getNewValue()));

        framesView.setItems(frames);
    }
}
