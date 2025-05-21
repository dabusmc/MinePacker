module dabusmc.minepacker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires json.simple;

    opens dabusmc.minepacker to javafx.fxml;
    exports dabusmc.minepacker;
}