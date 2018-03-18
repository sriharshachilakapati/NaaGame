package com.naagame.editor.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ReorderableListCell<T> extends ListCell<T> {

    private static final DataFormat customTFormat = new DataFormat("reorderableCellCustomFormat");

    private Function<T, String> transformer;

    @SuppressWarnings("unchecked")
    public ReorderableListCell(Function<T, String> transformer) {
        ListCell<T> thisCell = this;
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
                ObservableList<T> items = getListView().getItems();

                T draggedObject = (T) db.getContent(customTFormat);
                int draggedIdx = items.indexOf(draggedObject);
                int thisIdx = items.indexOf(getItem());

                Platform.runLater(() -> {
                    List<T> itemsCopy = new ArrayList<>(getListView().getItems());

                    itemsCopy.set(draggedIdx, getItem());
                    itemsCopy.set(thisIdx, draggedObject);

                    getListView().getItems().setAll(itemsCopy);
                });

                success = true;
            }

            event.setDropCompleted(success);

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            setText(transformer.apply(item));
        } else {
            setText(null);
        }
    }
}