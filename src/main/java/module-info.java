module dabusmc.minepacker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires json.simple;
    requires java.desktop;
    requires jdk.httpserver;
    requires jlhttp;

    opens dabusmc.minepacker to javafx.fxml;
    exports dabusmc.minepacker;
}