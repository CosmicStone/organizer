package com.organizer.gui;

import com.organizer.gui.component.ActionBarPanel;
import com.organizer.gui.component.FilePanel;
import com.organizer.gui.component.FolderInfoPanel;
import com.organizer.gui.component.MenuBarPanel;
import com.organizer.gui.component.ProgressPanel;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainWindow {
    // === 根节点 ===
    private final BorderPane root = new BorderPane();

    private final MenuBarPanel menu = new MenuBarPanel();
    private final ActionBarPanel action = new ActionBarPanel();
    private final ProgressPanel progress = new ProgressPanel();
    private final FolderInfoPanel folder = new FolderInfoPanel();
    private final FilePanel filePanel = new FilePanel();

    private final MainController controller;

    public MainWindow() {
        VBox top = new VBox(menu.getRoot(), action.getRoot(), progress.getRoot());
        root.setTop(top);

        HBox center = new HBox(folder.getRoot(), filePanel.getRoot());
        HBox.setHgrow(filePanel.getRoot(), Priority.ALWAYS);
        root.setCenter(center);

        controller = new MainController(this);
    }

    public BorderPane getRoot() {
        return root;
    }

    public ActionBarPanel getActionBarPanel() {
        return action;
    }
    public FolderInfoPanel getFolderInfoPanel() {
        return folder;
    }
    public FilePanel getFilePanel() {
        return filePanel;
    }
    public ProgressPanel getProgressPanel() {
        return progress;
    }
    public MenuBarPanel getMenuBarPanel() {
        return menu;
    }
}
