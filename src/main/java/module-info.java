module dabusmc.minepacker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens dabusmc.minepacker to javafx.fxml;
    exports dabusmc.minepacker;
}