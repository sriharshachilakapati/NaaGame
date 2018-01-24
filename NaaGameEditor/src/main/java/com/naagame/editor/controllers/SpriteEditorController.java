package com.naagame.editor.controllers;

import com.naagame.editor.model.Resources;
import com.naagame.editor.model.resources.IResource;
import com.naagame.editor.model.resources.Sprite;
import com.naagame.editor.model.resources.Texture;
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

    @FXML TableView<Sprite.Frame> framesView;
    @FXML TableColumn<Sprite.Frame, String> textureColumn;
    @FXML TableColumn<Sprite.Frame, Integer> durationColumn;

    private ObservableList<String> textures;
    private ObservableList<Sprite.Frame> frames;
    private Sprite currentSprite;

    @Override
    public void init(String name) {
        currentSprite = Resources.find(Resources.sprites, name);
        nameField.setText(name);

        textures.clear();
        textures.addAll(Resources.textures
                .stream().map(IResource::getName).collect(Collectors.toList()));

        frames.clear();
        frames.addAll(currentSprite.getFrames());
    }

    @FXML
    public void onAddFrameButtonClicked() {
        if (Resources.textures.size() == 0) {
            return;
        }

        frames.add(new Sprite.Frame(Resources.textures.get(0), 250));
    }

    @FXML
    public void onRemoveFrameButtonClicked() {
        Sprite.Frame selected = framesView.getSelectionModel().getSelectedItem();

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
            Sprite.Frame frame = param.getValue();
            Texture texture = frame.getTexture();
            return new SimpleObjectProperty<>(texture == null ? "" : texture.getName());
        });
        textureColumn.setCellFactory(ComboBoxTableCell.forTableColumn(textures));
        textureColumn.setOnEditCommit(event -> event.getRowValue()
                .setTexture(Resources.find(Resources.textures, event.getNewValue())));

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
