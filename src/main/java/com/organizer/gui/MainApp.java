package com.organizer.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        MainWindow window = new MainWindow();

        Scene scene = new Scene(window.getRoot(), 900, 650);

        stage.setTitle("文件整理器");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
