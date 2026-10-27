module com.organizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    // requires javafx.media;  // 如果用了 media

    opens com.organizer.gui to javafx.fxml;
    exports com.organizer.gui;
    exports com.organizer.cli;
    exports com.organizer.log;
    exports com.organizer.core;
}
