package com.organizer.core;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileOrganizer {

    private final Path root;
    private final boolean recursive;
    private final FileTypeClassifier classifier;

    public FileOrganizer(Path root, boolean recursive) {
        this.root = root.toAbsolutePath().normalize();
        this.recursive = recursive;
        this.classifier = new FileTypeClassifier();
    }

    public OrganizePreview preview() throws IOException {
        if (!Files.isDirectory(root)) {
            throw new IOException("目标不是目录: " + root);
        }
        List<OrganizePlan> plans = new ArrayList<>();
        if (recursive) {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    handleFile(file, plans);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (isCategoryDirectory(dir)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            try (var stream = Files.newDirectoryStream(root)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        handleFile(file, plans);
                    }
                }
            }
        }
        return new OrganizePreview(root, plans);
    }

    public int execute(OrganizePreview preview) throws IOException {
        int moved = 0;
        for (OrganizePlan plan : preview.getPlans()) {
            Path target = plan.getTarget();
            Files.createDirectories(target.getParent());
            Path resolved = resolveConflict(target);
            Files.move(plan.getSource(), resolved, StandardCopyOption.REPLACE_EXISTING);
            moved++;
        }
        return moved;
    }

    private void handleFile(Path file, List<OrganizePlan> plans) {
        if (isInsideCategoryDirectory(file)) {
            return;
        }
        FileCategory category = classifier.classify(file);
        Path targetDir = root.resolve(category.getLabel());
        Path target = targetDir.resolve(file.getFileName());
        plans.add(new OrganizePlan(file, target, category));
    }

    private boolean isCategoryDirectory(Path dir) {
        if (!dir.getParent().equals(root)) {
            return false;
        }
        String name = dir.getFileName().toString();
        for (FileCategory c : FileCategory.values()) {
            if (c.getLabel().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInsideCategoryDirectory(Path file) {
        Path parent = file.getParent();
        while (parent != null && !parent.equals(root)) {
            if (isCategoryDirectory(parent)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    private Path resolveConflict(Path target) throws IOException {
        if (!Files.exists(target)) {
            return target;
        }
        String fileName = target.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String base = dot > 0 ? fileName.substring(0, dot) : fileName;
        String ext = dot > 0 ? fileName.substring(dot) : "";
        int i = 1;
        Path candidate;
        do {
            candidate = target.resolveSibling(base + "(" + i + ")" + ext);
            i++;
        } while (Files.exists(candidate));
        return candidate;
    }
}