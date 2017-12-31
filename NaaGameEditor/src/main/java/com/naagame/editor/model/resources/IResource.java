package com.naagame.editor.model.resources;

import com.shc.easyjson.JSONValue;

public interface IResource {
    JSONValue toJSON();

    String getName();
}
