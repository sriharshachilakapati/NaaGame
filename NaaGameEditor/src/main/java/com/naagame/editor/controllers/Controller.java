package com.naagame.editor.controllers;

public abstract class Controller {

    private MainController mainController;

    protected void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    protected void notifySave() {
        mainController.resourcesChanged();
    }

    protected boolean hasUnsavedEdits() {
        return false;
    }

    protected void commitChanges() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void discardChanges() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void init(String resourceName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void resourcesChanged() {
    }
}
