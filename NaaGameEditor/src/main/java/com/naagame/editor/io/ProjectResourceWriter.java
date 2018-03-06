package com.naagame.editor.io;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProjectResourceWriter {
    public static String writeFile(Path file, Path parentDir) throws IOException {
        String hash = hash(file.toAbsolutePath());
        Path newPath = parentDir.resolve("resources").resolve(hash + "." + getExtension(file));

        if (!Files.exists(newPath)) {
            Files.copy(file, newPath);
        }

        return parentDir.relativize(newPath).toString();
    }

    private static String getExtension(Path file) {
        return file.toAbsolutePath().toString().replaceAll(".*\\.", "");
    }

    private static String hash(Path path) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(Files.readAllBytes(path));
            return String.format("%032x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
