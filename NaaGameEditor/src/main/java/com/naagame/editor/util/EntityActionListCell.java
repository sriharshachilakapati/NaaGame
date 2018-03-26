package com.naagame.editor.util;

import com.naagame.core.resources.NgmEntity;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EntityActionListCell extends ListCell<NgmEntity.Event.Action> {

    private static final DataFormat customTFormat = new DataFormat("reorderableCellCustomFormat");

    private Function<NgmEntity.Event.Action, String> transformer;

    @SuppressWarnings("unchecked")
    public EntityActionListCell(Function<NgmEntity.Event.Action, String> transformer, Runnable callback) {
        ListCell<NgmEntity.Event.Action> thisCell = this;
        this.transformer = transformer;

        setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.put(customTFormat, getItem());

            dragboard.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasContent(customTFormat)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasContent(customTFormat)) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasContent(customTFormat)) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasContent(customTFormat)) {
                ObservableList<NgmEntity.Event.Action> items = getListView().getItems();

                NgmEntity.Event.Action draggedObject = (NgmEntity.Event.Action) db.getContent(customTFormat);
                int draggedIdx = items.indexOf(draggedObject);
                int thisIdx = items.indexOf(getItem());

                Platform.runLater(() -> {
                    List<NgmEntity.Event.Action> itemsCopy = new ArrayList<>(getListView().getItems());

                    itemsCopy.set(draggedIdx, getItem());
                    itemsCopy.set(thisIdx, draggedObject);

                    getListView().getItems().setAll(itemsCopy);
                });

                success = true;
            }

            if (success) {
                callback.run();
            }

            event.setDropCompleted(success);
            event.consume();
        });

        setOnDragDone(DragEvent::consume);

        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                NgmEntity.Event.Action action = getItem();

                if (ActionEditor.edit(ProjectUtils.findDefinition(action.getCode()), action)) {
                    updateItem(action, false);
                    callback.run();
                }
            }
        });

        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(evt -> {
            getListView().getItems().remove(getItem());
            callback.run();
        });

        menu.getItems().add(delete);

        setContextMenu(menu);
    }

    @Override
    protected void updateItem(NgmEntity.Event.Action item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            setText(transformer.apply(item));
        } else {
            setText(null);
        }
    }
}