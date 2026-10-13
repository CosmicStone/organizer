package com.organizer.gui.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.organizer.core.FileCategory;
import com.organizer.core.OrganizePlan;
import com.organizer.gui.FileItem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FilePanel {
    private final VBox root = new VBox();

    private HBox toolbar;

    private final CheckBox cbSelectAll = new CheckBox("全选");
    private final Label lblSelectedCount = new Label("已选 0 / 0 个文件");
    private final Button btnBackToFlat = new Button("返回选择");

    private final StackPane fileAreaStack;
    private final ScrollPane flatListPane;   // 扁平模式
    private final ScrollPane previewTreePane; // 预览模式
    private final VBox flatListBox = new VBox();
    private final VBox previewTreeBox = new VBox();
    
    // 数据
    private List<FileItem> files = new ArrayList<>();
    private boolean previewMode = false;

    // 控件引用缓存
    private final Map<FileItem, CheckBox> flatCheckboxes = new HashMap<>();
    private final Map<FileItem, CheckBox> treeCheckboxes = new HashMap<>();
    private final Map<FileCategory, CheckBox> parentCheckboxes = new HashMap<>();

    public FilePanel() {
        // ---- 工具行 ----
        HBox toolbar = new HBox(15, cbSelectAll, lblSelectedCount, btnBackToFlat);
        toolbar.setPadding(new Insets(8, 12, 8, 12));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        btnBackToFlat.setVisible(false);

        // ---- 扁平列表 ----
        flatListPane = new ScrollPane(flatListBox);
        flatListPane.setFitToWidth(true);
        flatListPane.setStyle("-fx-background-color: transparent;");

        // ---- 预览树 ----
        previewTreePane = new ScrollPane(previewTreeBox);
        previewTreePane.setFitToWidth(true);
        previewTreePane.setStyle("-fx-background-color: transparent;");
        previewTreePane.setVisible(false);

        // ---- 叠放 ----
        fileAreaStack = new StackPane(flatListPane, previewTreePane);
        VBox.setVgrow(fileAreaStack, Priority.ALWAYS);

        showPlaceholder("请先点击\"选择文件夹\"按钮");

        root.getChildren().addAll(toolbar, fileAreaStack);
        VBox.setVgrow(root, Priority.ALWAYS);

        // ---- 事件 ----
        cbSelectAll.setOnAction(e -> {
            boolean all = cbSelectAll.isSelected();
            cbSelectAll.setIndeterminate(false);
            if (all) {
                selectAllItems();
            } else {
                clearSelection();
            }
        });
        btnBackToFlat.setOnAction(e -> showFlatList());
    }

    public VBox getRoot() {
        return root;
    }

    // 数据入口：选择文件夹后调用
    public void setFiles(List<FileItem> items) {
        this.files = items;
        this.previewMode = false;

        cbSelectAll.setSelected(false);
        cbSelectAll.setIndeterminate(false);
        btnBackToFlat.setVisible(false);
        flatListPane.setVisible(true);
        previewTreePane.setVisible(false);

        renderFlatList();
        updateSelectionCount();
    }

    // 数据入口：点击"预览结果"后调用
    public void showPreviewTree(Map<FileCategory, List<OrganizePlan>> grouped,
                                List<FileItem> items) {
        // plan -> FileItem 映射，保留勾选状态
        Map<OrganizePlan, FileItem> itemMap = new LinkedHashMap<>();
        for (FileItem fi : items) {
            itemMap.put(fi.getPlan(), fi);
        }

        previewTreeBox.getChildren().clear();
        treeCheckboxes.clear();
        parentCheckboxes.clear();

        if (grouped.isEmpty()) {
            Label tip = new Label("没有可整理的文件");
            tip.setStyle("-fx-text-fill: #888888;");
            StackPane wrap = new StackPane(tip);
            wrap.setMinHeight(200);
            previewTreeBox.getChildren().add(wrap);
        } else {
            for (Map.Entry<FileCategory, List<OrganizePlan>> entry : grouped.entrySet()) {
                List<FileItem> children = new ArrayList<>();
                for (OrganizePlan p : entry.getValue()) {
                    FileItem fi = itemMap.get(p);
                    if (fi != null) {
                        children.add(fi);
                    }
                }
                previewTreeBox.getChildren().add(buildCategoryGroup(entry.getKey(), children));
            }
        }

        previewMode = true;
        flatListPane.setVisible(false);
        previewTreePane.setVisible(true);
        btnBackToFlat.setVisible(true);

        syncSelectAllCheckbox();
        updateSelectionCount();
    }

    // 切回扁平列表
    public void showFlatList() {
        previewMode = false;
        flatListPane.setVisible(true);
        previewTreePane.setVisible(false);
        btnBackToFlat.setVisible(false);

        // 重新渲染，从 FileItem 恢复勾选状态
        renderFlatList();
        syncSelectAllCheckbox();
        updateSelectionCount();
    }

    // 扁平列表渲染
    private void renderFlatList() {
        flatListBox.getChildren().clear();
        flatCheckboxes.clear();

        if (files.isEmpty()) {
            showPlaceholder("该文件夹下没有可整理的文件");
            return;
        }

        for (FileItem item : files) {
            flatListBox.getChildren().add(createFlatRow(item));
        }
    }

    private HBox createFlatRow(FileItem item) {
        CheckBox cb = new CheckBox();
        cb.setSelected(item.isSelected());
        cb.setOnAction(e -> {
            item.setSelected(cb.isSelected());
            syncSelectAllCheckbox();
            updateSelectionCount();
        });
        flatCheckboxes.put(item, cb);

        Label icon = new Label("📄");
        Label name = new Label(item.getName());

        HBox row = new HBox(8, cb, icon, name);
        row.setPadding(new Insets(6, 12, 6, 12));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // 预览树渲染
    private VBox buildCategoryGroup(FileCategory category, List<FileItem> children) {
        VBox groupBox = new VBox();

        // ---- 父节点行 ----
        CheckBox parentCb = new CheckBox();
        parentCheckboxes.put(category, parentCb);

        Label parentIcon = new Label("📁");
        Label parentName = new Label(category.getLabel() + "/");
        parentName.setStyle("-fx-font-weight: bold;");
        Label parentCount = new Label("(" + children.size() + " 个)");
        parentCount.setStyle("-fx-text-fill: #666666;");

        HBox parentRow = new HBox(8, parentCb, parentIcon, parentName, parentCount);
        parentRow.setPadding(new Insets(6, 12, 6, 12));
        parentRow.setAlignment(Pos.CENTER_LEFT);
        parentRow.setStyle("-fx-background-color: #f5f5f5;");

        // ---- 子节点容器 ----
        VBox childrenBox = new VBox();
        for (FileItem child : children) {
            childrenBox.getChildren().add(createChildRow(child, category, children));
        }

        // ---- 初始父节点状态 ----
        updateParentCheckbox(category, children);

        // ---- 父节点点击：全选 / 全不选 ----
        parentCb.setOnAction(e -> {
            boolean select = parentCb.isSelected();
            parentCb.setIndeterminate(false);
            for (FileItem child : children) {
                child.setSelected(select);
            }
            refreshChildrenCheckboxes(children);
            syncSelectAllCheckbox();
            updateSelectionCount();
        });

        // ---- 点击父行空白区域折叠 / 展开 ----
        parentRow.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof CheckBox) {
                return;
            }
            boolean visible = !childrenBox.isVisible();
            childrenBox.setVisible(visible);
            childrenBox.setManaged(visible);
        });

        groupBox.getChildren().addAll(parentRow, childrenBox);
        return groupBox;
    }

    private HBox createChildRow(FileItem item, FileCategory category, List<FileItem> siblings) {
        CheckBox cb = new CheckBox();
        cb.setSelected(item.isSelected());
        cb.setOnAction(e -> {
            item.setSelected(cb.isSelected());
            updateParentCheckbox(category, siblings);
            syncSelectAllCheckbox();
            updateSelectionCount();
        });
        treeCheckboxes.put(item, cb);

        Label icon = new Label("📄");
        Label name = new Label(item.getName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #999999;");
        Label target = new Label(item.getTargetFolderLabel() + "/");
        target.setStyle("-fx-text-fill: #4a90d9;");

        HBox row = new HBox(8, cb, icon, name, spacer, arrow, target);
        row.setPadding(new Insets(4, 12, 4, 36));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // 复选框状态同步
    private void refreshChildrenCheckboxes(List<FileItem> children) {
        for (FileItem child : children) {
            CheckBox cb = treeCheckboxes.get(child);
            if (cb != null) {
                cb.setSelected(child.isSelected());
            }
        }
    }

    private void updateParentCheckbox(FileCategory category, List<FileItem> children) {
        CheckBox parentCb = parentCheckboxes.get(category);
        if (parentCb == null || children.isEmpty()) {
            return;
        }
        long selected = children.stream().filter(FileItem::isSelected).count();
        if (selected == 0) {
            parentCb.setSelected(false);
            parentCb.setIndeterminate(false);
        } else if (selected == children.size()) {
            parentCb.setSelected(true);
            parentCb.setIndeterminate(false);
        } else {
            parentCb.setSelected(false);
            parentCb.setIndeterminate(true);
        }
    }

    private void syncSelectAllCheckbox() {
        long selected = files.stream().filter(FileItem::isSelected).count();
        if (files.isEmpty() || selected == 0) {
            cbSelectAll.setSelected(false);
            cbSelectAll.setIndeterminate(false);
        } else if (selected == files.size()) {
            cbSelectAll.setSelected(true);
            cbSelectAll.setIndeterminate(false);
        } else {
            cbSelectAll.setSelected(false);
            cbSelectAll.setIndeterminate(true);
        }
    }

    private void updateSelectionCount() {
        long selected = files.stream().filter(FileItem::isSelected).count();
        lblSelectedCount.setText("已选 " + selected + " / " + files.size() + " 个文件");
    }

    // 全选
    private void onSelectAll() {
        boolean all = cbSelectAll.isSelected();
        for (FileItem f : files) {
            f.setSelected(all);
        }

        if (previewMode) {
            for (Map.Entry<FileItem, CheckBox> e : treeCheckboxes.entrySet()) {
                e.getValue().setSelected(e.getKey().isSelected());
            }
            for (FileCategory cat : parentCheckboxes.keySet()) {
                List<FileItem> children = files.stream()
                        .filter(f -> f.getPlan().getCategory() == cat)
                        .toList();
                updateParentCheckbox(cat, children);
            }
        } else {
            for (Map.Entry<FileItem, CheckBox> e : flatCheckboxes.entrySet()) {
                e.getValue().setSelected(e.getKey().isSelected());
            }
        }

        updateSelectionCount();
    }

    // 占位提示
    private void showPlaceholder(String text) {
        flatListBox.getChildren().clear();
        Label tip = new Label(text);
        tip.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");
        StackPane wrap = new StackPane(tip);
        wrap.setMinHeight(200);
        flatListBox.getChildren().add(wrap);
    }
    
    public void selectAllItems() {
        applySelection(f -> true);
    }

    public void clearSelection() {
        applySelection(f -> false);
    }

    public void invertSelection() {
        applySelection(f -> !f.isSelected());
    }

    private void applySelection(java.util.function.Predicate<FileItem> rule) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (FileItem f : files) {
            f.setSelected(rule.test(f));
        }
        refreshAllCheckboxes();
        syncSelectAllCheckbox();
        updateSelectionCount();
    }

    /** 根据当前模式刷新所有复选框显示 */
    private void refreshAllCheckboxes() {
        if (previewMode) {
            for (var e : treeCheckboxes.entrySet()) {
                e.getValue().setSelected(e.getKey().isSelected());
            }
            for (FileCategory cat : parentCheckboxes.keySet()) {
                List<FileItem> children = files.stream()
                        .filter(f -> f.getPlan().getCategory() == cat)
                        .toList();
                updateParentCheckbox(cat, children);
            }
        } else {
            for (var e : flatCheckboxes.entrySet()) {
                e.getValue().setSelected(e.getKey().isSelected());
            }
        }
    }
}