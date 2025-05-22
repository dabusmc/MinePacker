package dabusmc.minepacker;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.data.projects.MinecraftGenerator;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApiType;
import dabusmc.minepacker.backend.serialization.Serializer;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.page.SecondTestPage;
import dabusmc.minepacker.frontend.page.TestPage;
import javafx.application.Application;
import javafx.stage.Stage;

public class MinePackerApp extends Application {

    @Override
    public void init() {
        // Initialise Runtime
        new MinePackerRuntime();
        MinePackerRuntime.s_Instance.setLogLevel(LogLevel.MESSAGE);
        MinePackerRuntime.s_Instance.constructModApi(ModApiType.Modrinth);
        MinePackerRuntime.s_Instance.setCurrentProject(Project.generateDefaultProject());

        // Initialise Data
        Logger.info("MinePackerApp",
                MinePackerRuntime.s_Instance.getMCGenerator().getJsonPathForVersion(MinePackerRuntime.s_Instance.getCurrentProject().getMinecraftVersion()));

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

    @Override
    public void stop() {
        Serializer.save(MinePackerRuntime.s_Instance.getSettings());
    }

    public static void main(String[] args) {
        launch();
    }
}