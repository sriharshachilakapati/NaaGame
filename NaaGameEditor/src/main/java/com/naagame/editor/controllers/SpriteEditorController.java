package com.naagame.editor.controllers;

import com.naagame.editor.model.Resources;
import com.naagame.editor.model.resources.IResource;
import com.naagame.editor.model.resources.Sprite;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpriteEditorController implements IController {
    String currentSprite;

    @FXML TextField nameField;
    @FXML TableView<Sprite.Frame> framesView;
    @FXML TableColumn<Sprite.Frame, String> textureColumn;
    @FXML TableColumn<Sprite.Frame, Integer> durationColumn;

    public void init() {
        nameField.setText(currentSprite);

        ObservableList<String> textures = FXCollections.observableList(Resources.textures
                .stream().map(IResource::getName).collect(Collectors.toList()));

        textureColumn.setCellValueFactory(param -> {
            Sprite.Frame frame = param.getValue();
            return new SimpleObjectProperty<>(frame.getTexture().getName());
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

        ObservableList<Sprite.Frame> frames = FXCollections.observableArrayList(
                IntStream.range(0, 10).mapToObj(i -> new Sprite.Frame(Resources.textures.get(i), 250))
                        .collect(Collectors.toList()));

        framesView.setItems(frames);
    }

    @FXML
    public void onAddFrameButtonClicked() {
        System.out.println("Add frame");
    }

    @FXML
    public void onRemoveFrameButtonClicked() {
        System.out.println("Remove frame");
    }
}
