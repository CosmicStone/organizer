package com.organizer.gui.component;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class MenuBarPanel {
    private final MenuBar root;

    // 文件
    private final MenuItem itemChooseFolder = new MenuItem("选择文件夹...");
    private final MenuItem itemRefresh = new MenuItem("重新扫描");
    private final MenuItem itemExit = new MenuItem("退出");

    // 编辑
    private final MenuItem itemSelectAll = new MenuItem("全选");
    private final MenuItem itemDeselectAll = new MenuItem("取消全选");
    private final MenuItem itemInvertSelection = new MenuItem("反选");

    // 视图
    private final MenuItem itemFlatView = new MenuItem("列表视图");
    private final MenuItem itemPreviewView = new MenuItem("预览视图");

    // 帮助
    private final MenuItem itemHelp = new MenuItem("使用说明");
    private final MenuItem itemAbout = new MenuItem("关于");

    public MenuBarPanel() {
        // ---- 文件 ----
        itemChooseFolder.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        itemRefresh.setAccelerator(KeyCombination.keyCombination("F5"));
        itemExit.setAccelerator(KeyCombination.keyCombination("Shortcut+Q"));

        Menu menuFile = new Menu("文件");
        menuFile.getItems().addAll(
                itemChooseFolder,
                itemRefresh,
                new SeparatorMenuItem(),
                itemExit
        );

        // ---- 编辑 ----
        itemSelectAll.setAccelerator(KeyCombination.keyCombination("Shortcut+A"));
        itemDeselectAll.setAccelerator(KeyCombination.keyCombination("Shortcut+Shift+A"));
        itemInvertSelection.setAccelerator(KeyCombination.keyCombination("Shortcut+I"));

        Menu menuEdit = new Menu("编辑");
        menuEdit.getItems().addAll(
                itemSelectAll,
                itemDeselectAll,
                new SeparatorMenuItem(),
                itemInvertSelection
        );

        // ---- 视图 ----
        Menu menuView = new Menu("视图");
        menuView.getItems().addAll(itemFlatView, itemPreviewView);

        // ---- 帮助 ----
        itemHelp.setAccelerator(KeyCombination.keyCombination("F1"));

        Menu menuHelp = new Menu("帮助");
        menuHelp.getItems().addAll(itemHelp, itemAbout);

        root = new MenuBar(menuFile, menuEdit, menuView, menuHelp);

        // 初始：未选文件夹，相关项禁用
        setFileLoaded(false);
    }

    /** 是否已加载文件夹，用来统一控制菜单项可用性 */
    public void setFileLoaded(boolean loaded) {
        itemRefresh.setDisable(!loaded);
        itemSelectAll.setDisable(!loaded);
        itemDeselectAll.setDisable(!loaded);
        itemInvertSelection.setDisable(!loaded);
        itemFlatView.setDisable(!loaded);
        itemPreviewView.setDisable(!loaded);
    }

    /** 是否已加载预览，用来统一控制菜单项可用性 */
    public void setPreviewed(boolean loaded) {
        itemPreviewView.setDisable(!loaded);
    }

    public MenuBar getRoot() { return root; }

    public MenuItem getItemChooseFolder()   { return itemChooseFolder; }
    public MenuItem getItemRefresh()        { return itemRefresh; }
    public MenuItem getItemExit()           { return itemExit; }
    public MenuItem getItemSelectAll()      { return itemSelectAll; }
    public MenuItem getItemDeselectAll()    { return itemDeselectAll; }
    public MenuItem getItemInvertSelection(){ return itemInvertSelection; }
    public MenuItem getItemFlatView()       { return itemFlatView; }
    public MenuItem getItemPreviewView()    { return itemPreviewView; }
    public MenuItem getItemHelp()           { return itemHelp; }
    public MenuItem getItemAbout()          { return itemAbout; }
}

