package com.naagame.editor.util;

import com.naagame.core.NgmProject;
import com.naagame.core.action.ActionArgument;
import com.naagame.core.action.ActionDefinition;
import com.naagame.core.action.ActionTarget;
import com.naagame.core.action.ArgumentType;
import com.naagame.core.resources.NgmEntity;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class ActionEditor {
    public static <T> void edit(ActionDefinition<T> actionDef) {
        edit(actionDef, new NgmEntity.Event.Action(actionDef.getCode(),
                actionDef.encode(actionDef.getSupplier().get())));
    }

    private static <T> void edit(ActionDefinition<T> actionDef, NgmEntity.Event.Action action) {
        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane content = dialog.getDialogPane();

        dialog.setTitle(wordCamelCase(actionDef.getCode()).replaceFirst(".+?\\s", ""));

        ButtonType saveBtn = new ButtonType("Save");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        content.getButtonTypes().clear();
        content.getButtonTypes().addAll(saveBtn, cancelBtn);

        content.setHeaderText(actionDef.getDescription());

        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);

        int row = 0;

        T actionObject = actionDef.decode(action.getArgs(), actionDef.getSupplier().get());

        for (ActionArgument<T> argument : actionDef.getArguments()) {
            layout.add(new Label(String.format("Argument %d", row + 1)), 0, row);

            ArgumentType<?> argumentType = argument.getType();

            if (argumentType == ArgumentType.INTEGER) {
                Spinner<Integer> spinner = getIntegerSpinner(argument, actionObject);
                layout.add(spinner, 1, row);

            } else if (argumentType == ArgumentType.FLOAT) {
                Spinner<Double> spinner = getDoubleSpinner(argument, actionObject);
                layout.add(spinner, 1, row);

            } else if (argumentType == ArgumentType.BOOLEAN) {
                ChoiceBox<Boolean> choiceBox = getBooleanChoiceBox(argument, actionObject);
                layout.add(choiceBox, 1, row);

            } else if (argumentType == ArgumentType.STRING) {
                TextField textField = new TextField();
                textField.setText((String) argument.getGetter().apply(actionObject));
                textField.textProperty().addListener((ov, o, n) -> {
                    if (n != null) {
                        argument.getSetter().accept(actionObject, n);
                    }
                });

                layout.add(textField, 1, row);

            } else if (argumentType == ArgumentType.TARGET) {
                ChoiceBox<ActionTarget> choiceBox = getActionTargetChoiceBox(argument, actionObject);
                layout.add(choiceBox, 1, row);

            } else if (argumentType == ArgumentType.ENTITY) {
                ChoiceBox<NgmEntity> choiceBox = getEntityChoiceBox(argument, actionObject);
                layout.add(choiceBox, 1, row);
            }

            row++;
        }

        dialog.setResultConverter(x -> x);

        layout.setPadding(new Insets(50, 30, 50, 30));

        layout.getColumnConstraints().addAll(
                new ColumnConstraints(),
                new ColumnConstraints()
        );

        layout.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);
        layout.getColumnConstraints().get(1).setFillWidth(true);

        content.setPrefSize(350, 400);
        content.setContent(layout);

        dialog.showAndWait();
    }

    private static <T> ChoiceBox<NgmEntity> getEntityChoiceBox(ActionArgument<T> argument, T actionObject) {
        ChoiceBox<NgmEntity> choiceBox = new ChoiceBox<>();

        choiceBox.getItems().addAll(NgmProject.entities);
        choiceBox.setConverter(new StringConverter<NgmEntity>() {
            @Override
            public String toString(NgmEntity object) {
                return object == null ? "" : object.getName();
            }

            @Override
            public NgmEntity fromString(String string) {
                return NgmProject.find(NgmProject.entities, string);
            }
        });

        choiceBox.getSelectionModel().select((NgmEntity) argument.getGetter().apply(actionObject));
        choiceBox.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (n != null) {
                argument.getSetter().accept(actionObject, n);
            }
        });

        return choiceBox;
    }

    private static <T> ChoiceBox<ActionTarget> getActionTargetChoiceBox(ActionArgument<T> argument, T actionObject) {
        ChoiceBox<ActionTarget> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(ActionTarget.values());
        choiceBox.setConverter(new StringConverter<ActionTarget>() {
            @Override
            public String toString(ActionTarget object) {
                return object.toString();
            }

            @Override
            public ActionTarget fromString(String string) {
                return ActionTarget.valueOf(string);
            }
        });

        choiceBox.getSelectionModel().select((ActionTarget) argument.getGetter().apply(actionObject));
        choiceBox.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (n != null) {
                argument.getSetter().accept(actionObject, n);
            }
        });

        return choiceBox;
    }

    private static <T> ChoiceBox<Boolean> getBooleanChoiceBox(ActionArgument<T> argument, T actionObject) {
        ChoiceBox<Boolean> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(true, false);
        choiceBox.setConverter(new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean object) {
                return String.valueOf(object);
            }

            @Override
            public Boolean fromString(String string) {
                return Boolean.parseBoolean(string);
            }
        });

        choiceBox.getSelectionModel().select((Boolean) argument.getGetter().apply(actionObject));
        choiceBox.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            if (n != null) {
                argument.getSetter().accept(actionObject, n);
            }
        });

        return choiceBox;
    }

    private static <T> Spinner<Double> getDoubleSpinner(ActionArgument<T> argument, T actionObject) {
        Spinner<Double> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE,
                Double.MAX_VALUE, 0));

        spinner.setEditable(true);
        spinner.getEditor().setText(String.valueOf(argument.getGetter().apply(actionObject)));
        spinner.increment(0);

        spinner.focusedProperty().addListener((ov, o, n) -> spinner.increment(0));

        spinner.valueProperty().addListener((ov, o, n) -> {
            if (n != null) {
                argument.getSetter().accept(actionObject, (float) ((double) spinner.getValue()));
            }
        });

        return spinner;
    }

    private static <T> Spinner<Integer> getIntegerSpinner(ActionArgument<T> argument, T actionObject) {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE,
                Integer.MAX_VALUE, 0));

        spinner.setEditable(true);
        spinner.getEditor().setText(String.valueOf(argument.getGetter().apply(actionObject)));
        spinner.increment(0);

        spinner.focusedProperty().addListener((ov, o, n) -> spinner.increment(0));

        spinner.valueProperty().addListener((ov, o, n) -> {
            if (n != null) {
                argument.getSetter().accept(actionObject, spinner.getValue());
            }
        });

        return spinner;
    }

    private static String wordCamelCase(String str) {
        String[] parts = str.toLowerCase().split("\\s+|_");

        for (int i = 0; i < parts.length; i++) {
            parts[i] = Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1);
        }

        return String.join(" ", parts);
    }
}
