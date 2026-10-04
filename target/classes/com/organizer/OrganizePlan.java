package com.organizer.core;

import java.nio.file.Path;

public class OrganizePlan {
    private final Path source;
    private final Path target;
    private final FileCategory category;

    public OrganizePlan(Path source, Path target, FileCategory category) {
        this.source = source;
        this.target = target;
        this.category = category;
    }

    public Path getSource() {
        return source;
    }

    public Path getTarget() {
        return target;
    }

    public FileCategory getCategory() {
        return category;
    }
}