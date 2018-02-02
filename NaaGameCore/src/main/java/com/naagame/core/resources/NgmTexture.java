package com.naagame.core.resources;

public class NgmTexture implements IResource {
    private String source = "";
    private String name;

    public NgmTexture(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
