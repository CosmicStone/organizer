package com.organizer.gui.component;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FolderInfoPanel {
    private final VBox box;

    private Label lblFolderName;
    private Label lblFolderPath;
    private Label lblFileCount;

    public FolderInfoPanel() {
        build();
        box = new VBox(8, lblFolderName, lblFolderPath, lblFileCount);
        box.setPadding(new Insets(12));
        box.setPrefWidth(220);
        box.setMinWidth(220);
        box.setStyle("-fx-border-color: #dddddd; -fx-border-width: 0 1 0 0;");
    }
    
    private void build() {
        lblFolderName = new Label("未选择文件夹");
        lblFolderName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        lblFolderPath = new Label("");
        lblFolderPath.setWrapText(true);
        lblFolderPath.setStyle("-fx-text-fill: #666;");

        lblFileCount = new Label("");
    }

    /** 选择文件夹后调用，刷新左侧显示 */
    public void setFolder(File folder, int fileCount) {
        if (folder == null) {
            lblFolderName.setText("未选择文件夹");
            lblFolderPath.setText("");
            lblFileCount.setText("");
            return;
        }
        lblFolderName.setText("📁 " + folder.getName());
        lblFolderPath.setText(folder.getAbsolutePath());
        lblFileCount.setText("共 " + fileCount + " 个文件");
    }

    public VBox getRoot() {
        return box;
    }
}


