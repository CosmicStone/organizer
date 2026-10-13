package com.organizer.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.organizer.core.FileOrganizer;
import com.organizer.core.OrganizePlan;
import com.organizer.core.OrganizePreview;
import com.organizer.gui.component.MenuBarPanel;

import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

public class MainController {
    private final MainWindow window;

    private FileOrganizer currentOrganizer;
    private OrganizePreview currentPreview;
    private List<FileItem> fileItems;

    private File currentFolder;

    public MainController(MainWindow window) {
        this.window = window;
        bindEvents();
    }

    private void bindEvents() {
        // 原有按钮
        window.getActionBarPanel().getBtnChooseFolder().setOnAction(e -> onChooseFolder());
        window.getActionBarPanel().getBtnPreview().setOnAction(e -> onPreview());
        window.getActionBarPanel().getBtnOrganize().setOnAction(e -> onOrganize());

        // 菜单
        MenuBarPanel menu = window.getMenuBarPanel();
        menu.getItemChooseFolder().setOnAction(e -> onChooseFolder());
        menu.getItemRefresh().setOnAction(e -> onRefresh());
        menu.getItemExit().setOnAction(e -> onExit());
        menu.getItemSelectAll().setOnAction(e -> window.getFilePanel().selectAllItems());
        menu.getItemDeselectAll().setOnAction(e -> window.getFilePanel().clearSelection());
        menu.getItemInvertSelection().setOnAction(e -> window.getFilePanel().invertSelection());
        menu.getItemFlatView().setOnAction(e -> window.getFilePanel().showFlatList());
        menu.getItemPreviewView().setOnAction(e -> onPreview());
        menu.getItemHelp().setOnAction(e -> onHelp());
        menu.getItemAbout().setOnAction(e -> onAbout());
    }

    // 选择文件夹
    private void onChooseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择要整理的文件夹");

        Window owner = window.getActionBarPanel()
                .getBtnChooseFolder().getScene().getWindow();

        File folder = chooser.showDialog(owner);
        this.currentFolder = folder;
        if (folder == null) {
            return; // 用户取消
        }

        try {
            currentOrganizer = new FileOrganizer(folder.toPath(), false);
            currentPreview = currentOrganizer.preview();

            fileItems = currentPreview.getPlans().stream()
                    .map(FileItem::new)
                    .toList();

            // 左侧信息
            window.getFolderInfoPanel().setFolder(folder, currentPreview.getTotalCount());

            // 右侧列表
            window.getFilePanel().setFiles(fileItems);

            // 按钮状态
            window.getActionBarPanel().setFolderChosen(true);

            // 进度区复位
            window.getProgressPanel().getProgressBar().setProgress(0);
            window.getProgressPanel().getLblPercent().setText("0%");
            window.getProgressPanel().getLblStatus().setText("就绪");

            window.getMenuBarPanel().setFileLoaded(true);
        } catch (IOException ex) {
            showError("读取文件夹失败：" + ex.getMessage());
        }
    }

    // 预览结果（切换到树形）
    private void onPreview() {
        if (currentPreview == null) {
            return;
        }
        window.getFilePanel().showPreviewTree(
                currentPreview.groupByCategory(),
                fileItems
        );
    }

    // 整理（只处理勾选项）
    private void onOrganize() {
        if (currentOrganizer == null || fileItems == null) {
            return;
        }

        List<OrganizePlan> selectedPlans = fileItems.stream()
                .filter(FileItem::isSelected)
                .map(FileItem::getPlan)
                .toList();

        if (selectedPlans.isEmpty()) {
            showInfo("请先勾选要整理的文件");
            return;
        }

        try {
            int moved = currentOrganizer.execute(selectedPlans);

            window.getProgressPanel().getProgressBar().setProgress(1.0);
            window.getProgressPanel().getLblPercent().setText("100%");
            window.getProgressPanel().getLblStatus()
                    .setText("整理完成，共移动 " + moved + " 个文件");

            // 整理完成后，禁用预览/整理，避免重复操作
            window.getActionBarPanel().setFolderChosen(false);

            window.getMenuBarPanel().setFileLoaded(false);
        } catch (IOException ex) {
            showError("整理失败：" + ex.getMessage());
        }
    }
    
    // 重新扫描：复用当前文件夹重新 preview
    private void onRefresh() {
        if (currentFolder == null) {
            return;
        }
        try {
            currentOrganizer = new FileOrganizer(currentFolder.toPath(), false);
            currentPreview = currentOrganizer.preview();
            fileItems = currentPreview.getPlans().stream().map(FileItem::new).toList();

            window.getFolderInfoPanel().setFolder(currentFolder, currentPreview.getTotalCount());
            window.getFilePanel().setFiles(fileItems);
            window.getActionBarPanel().setFolderChosen(true);

            window.getProgressPanel().getProgressBar().setProgress(0);
            window.getProgressPanel().getLblPercent().setText("0%");
            window.getProgressPanel().getLblStatus().setText("就绪");

        } catch (IOException ex) {
            showError("重新扫描失败：" + ex.getMessage());
        }
    }

    // 退出
    private void onExit() {
        javafx.application.Platform.exit();
    }

    // 使用说明
    private void onHelp() {
        String text = """
                使用步骤：
                1. 点击"选择文件夹"选择要整理的目录。
                2. 在列表或预览树中勾选需要整理的文件。
                3. 点击"预览结果"查看每个文件的归属分类。
                4. 点击"整理"开始移动文件。
                """;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("使用说明");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    // 关于
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("文件整理器");
        alert.setContentText("版本 1.0\n按文件类型自动分类整理目录下的文件。");
        alert.showAndWait();
    }

    // 工具
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
