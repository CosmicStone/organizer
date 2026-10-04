module com.organizer {
    requires javafx.controls;
    requires javafx.fxml;
    // requires javafx.media;  // 如果用了 media

    opens com.organizer to javafx.fxml;
    exports com.organizer;
}
