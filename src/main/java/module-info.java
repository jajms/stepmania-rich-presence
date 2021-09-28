module com.jajms.stepmania_rich_presence {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk7;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.jajms.stepmania_rich_presence to javafx.fxml, javafx.controls;
    exports com.jajms.stepmania_rich_presence;
}