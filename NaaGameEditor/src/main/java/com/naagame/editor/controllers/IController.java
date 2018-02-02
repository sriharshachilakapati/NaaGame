package com.naagame.editor.controllers;

public interface IController {
    default boolean hasUnsavedEdits() {
        return false;
    }

    default void commitChanges() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default void discardChanges() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default void init(String resourceName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    default void resourcesChanged() {
    }
}
