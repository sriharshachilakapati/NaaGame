package com.naagame.core.resources;

public class Texture implements IResource {
    private String fileName = "";
    private String name;

    public Texture(String name) {
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
