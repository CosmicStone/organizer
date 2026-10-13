package com.organizer.gui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class ProgressPanel {
    private final HBox box;
    private ProgressBar progressBar;
    private Label lblPercent;
    private Label lblStatus;
    private Label lblTitle;

    public ProgressPanel() {
        build();
        box = new HBox(10, lblTitle, progressBar, lblPercent, lblStatus);
        box.setPadding(new Insets(0, 12, 12, 12));
        box.setAlignment(Pos.CENTER_LEFT);
    }

    private void build() {
        lblTitle = new Label("进度：");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(420);

        lblPercent = new Label("0%");
        lblStatus = new Label("就绪");
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
    public Label getLblPercent() {
        return lblPercent;
    }
    public Label getLblStatus() {
        return lblStatus;
    }
    public HBox getRoot() {
        return box;
    }
    
}
