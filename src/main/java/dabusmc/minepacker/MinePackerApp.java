package dabusmc.minepacker;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApiType;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.page.SecondTestPage;
import dabusmc.minepacker.frontend.page.TestPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class MinePackerApp extends Application {

    @Override
    public void init() {
        // Initialise Runtime
        new MinePackerRuntime();
        MinePackerRuntime.s_Instance.setLogLevel(LogLevel.MESSAGE);
        MinePackerRuntime.s_Instance.constructModApi(ModApiType.Modrinth);
        MinePackerRuntime.s_Instance.setCurrentProject(Project.generateDefaultProject());

        PackerFile temp = new PackerFile(PackerFile.getResource("/dabusmc/minepacker/version_manifest_v2.json"));

        JSONParser parser = new JSONParser();
        try {
            JSONObject data = (JSONObject)parser.parse(temp.getReader());
            Logger.info("MinePackerApp", data.get("latest"));
        } catch (IOException | ParseException e) {
            Logger.error("MinePackerApp", e.toString());
        }

        // Test
        Mod jei = MinePackerRuntime.s_Instance.getModApi().getModFromID("u6dRKJwZ");
        Logger.info("MinePackerApp", jei);
    }

    @Override
    public void start(Stage stage) {
        // Initialise Stage
        stage.setTitle("MinePacker");
        stage.setResizable(false);

        // Initialise Page Switcher
        new PageSwitcher(stage);
        PageSwitcher.s_Instance.registerPage("test", new TestPage());
        PageSwitcher.s_Instance.registerPage("second_test", new SecondTestPage());
    }

    public static void main(String[] args) {
        launch();
    }
}