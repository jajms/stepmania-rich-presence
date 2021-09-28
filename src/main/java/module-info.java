module com.jajms.stepmania_rich_presence {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk7;
    requires javafx.controls;
    requires javafx.fxml;
    requires discord.game.sdk4j;
    requires kotlinx.coroutines.core.jvm;
    requires FXTrayIcon;
    requires com.sun.jna.platform;

    opens com.jajms.stepmania_rich_presence to javafx.fxml, javafx.controls;
    exports com.jajms.stepmania_rich_presence;
}