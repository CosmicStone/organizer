package com.organizer.gui;

import com.organizer.core.OrganizePlan;

public class FileItem {
    private final OrganizePlan plan;
    private boolean selected;

    public FileItem(OrganizePlan plan) {
        this.plan = plan;
        this.selected = false;
    }

    public OrganizePlan getPlan() { return plan; }

    public String getName() {
        return plan.getSource().getFileName().toString();
    }

    public String getTargetFolderLabel() {
        return plan.getCategory().getLabel();
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
