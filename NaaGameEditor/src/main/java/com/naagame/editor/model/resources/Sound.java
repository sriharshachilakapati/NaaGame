package com.naagame.editor.model.resources;

public class Sound implements IResource {
    private String fileName = "";
    private String name;

    public Sound(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
