package dabusmc.minepacker;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApiType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MinePackerApp extends Application {

    @Override
    public void init() {
        // Initialise Runtime
        new MinePackerRuntime();
        MinePackerRuntime.s_Instance.setLogLevel(LogLevel.MESSAGE);
        MinePackerRuntime.s_Instance.constructModApi(ModApiType.Modrinth);

        // Test
        Mod jei = MinePackerRuntime.s_Instance.getModApi().getModFromID("u6dRKJwZ");
        Logger.info("MinePackerApp", jei);
    }

    @Override
    public void start(Stage stage) {
        // Initialise Stage
        stage.setTitle("MinePacker");
        stage.setResizable(false);

        // Initialise temp Scene
        StackPane pane = new StackPane();
        Label tempLabel = new Label("Welcome to MinePacker!");
        pane.getChildren().add(tempLabel);

        Scene temp = new Scene(pane, 640, 400);
        stage.setScene(temp);

        // Display
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}