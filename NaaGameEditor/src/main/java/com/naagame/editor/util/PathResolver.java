package com.naagame.editor.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathResolver {
    private static Path projectDir;

    public static void setProjectDir(Path projectDir) {
        PathResolver.projectDir = projectDir;
    }

    public static Path getProjectDir() {
        return projectDir;
    }

    public static Path resolve(Path path) {
        return resolve(path.toString());
    }

    public static Path resolve(String path) {
        if (path.startsWith("resources")) {
            return projectDir.resolve(path);
        } else {
            return Paths.get(path);
        }
    }
}
