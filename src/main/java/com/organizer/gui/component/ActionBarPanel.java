package com.organizer.gui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ActionBarPanel {
    private final HBox box;
    private Button btnChooseFolder;
    private Button btnPreview;
    private Button btnOrganize;

    public ActionBarPanel() {
        build();
        box = new HBox(10, btnChooseFolder, btnPreview, btnOrganize);
        box.setPadding(new Insets(10, 12, 10, 12));
        box.setAlignment(Pos.CENTER_LEFT);
    }

    private void build() {
        btnChooseFolder = new Button("选择文件夹");
        btnPreview = new Button("预览结果");
        btnOrganize = new Button("整理");

        // 初始状态：未选文件夹，预览/整理不可用
        btnPreview.setDisable(true);
        btnOrganize.setDisable(true);
    }

    public Button getBtnChooseFolder() { 
        return btnChooseFolder;
    }
    public Button getBtnPreview() {
        return btnPreview;
    }
    public Button getBtnOrganize() {
        return btnOrganize;
    }
    public HBox getRoot() {
        return box;
    }

    /** 选择文件夹成功后，启用预览和整理 */
    public void setFolderChosen(boolean chosen) {
        btnPreview.setDisable(!chosen);
        btnOrganize.setDisable(!chosen);
    }
}
