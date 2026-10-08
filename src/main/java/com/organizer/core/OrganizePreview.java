package com.organizer.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OrganizePreview {
    private final Path root;
    private final List<OrganizePlan> plans;

    public OrganizePreview(Path root, List<OrganizePlan> plans) {
        this.root = root;
        this.plans = plans;
    }

    public Path getRoot() {
        return root;
    }

    public List<OrganizePlan> getPlans() {
        return plans;
    }

    public int getTotalCount() {
        return plans.size();
    }

    public Map<FileCategory, List<OrganizePlan>> groupByCategory() {
        Map<FileCategory, List<OrganizePlan>> grouped = new EnumMap<>(FileCategory.class);
        for (OrganizePlan plan : plans) {
            grouped.computeIfAbsent(plan.getCategory(), k -> new ArrayList<>()).add(plan);
        }
        return grouped;
    }

    public String toPreviewText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== 整理预览 =====\n");
        sb.append("目标目录: ").append(root.toAbsolutePath()).append("\n");
        sb.append("待整理文件数: ").append(plans.size()).append("\n\n");

        if (plans.isEmpty()) {
            sb.append("（没有需要整理的文件）\n");
            return sb.toString();
        }

        Map<FileCategory, List<OrganizePlan>> grouped = groupByCategory();
        for (Map.Entry<FileCategory, List<OrganizePlan>> entry : grouped.entrySet()) {
            FileCategory category = entry.getKey();
            List<OrganizePlan> list = entry.getValue();
            sb.append("[").append(category.getLabel()).append("] ").append(list.size()).append(" 个\n");
            for (OrganizePlan plan : list) {
                sb.append("  ").append(plan.getSource().getFileName())
                  .append("  ->  ").append(root.relativize(plan.getTarget())).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}