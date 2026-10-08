package com.organizer.core;

import java.nio.file.Path;

public class FileTypeClassifier {

    public FileCategory classify(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return FileCategory.OTHERS;
        }
        String ext = name.substring(dot + 1).toLowerCase();
        for (FileCategory category : FileCategory.values()) {
            if (category.getExtensions().contains(ext)) {
                return category;
            }
        }
        return FileCategory.OTHERS;
    }
}