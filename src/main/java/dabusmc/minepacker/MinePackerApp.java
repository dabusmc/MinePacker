package dabusmc.minepacker;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.analytics.Analytics;
import dabusmc.minepacker.backend.analytics.PerformanceProfile;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersion;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersionConverter;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.http.ModApiType;
import dabusmc.minepacker.backend.io.serialization.Serializer;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.page.ProjectPage;
import dabusmc.minepacker.frontend.page.ProjectSelectionPage;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MinePackerApp extends Application {

    @Override
    public void init() {
        // Prepare Analytics
        Analytics.init();

        // Initialise Runtime
        new MinePackerRuntime();
        MinePackerRuntime.s_Instance.setLogLevel(LogLevel.MESSAGE);
        MinePackerRuntime.s_Instance.constructModApi(ModApiType.Modrinth);
        MinePackerRuntime.s_Instance.setCurrentProject(Project.generateDefaultProject());

        // Initialise Test Project
        MinePackerRuntime.s_Instance.getCurrentProject().setName("Test Project");
        MinePackerRuntime.s_Instance.getCurrentProject().setVersion("1.0.0");
        MinePackerRuntime.s_Instance.getCurrentProject().setMinecraftVersion(MinecraftVersion.MC_1_21_5);
        MinePackerRuntime.s_Instance.getCurrentProject().setLoader(Mod.Loader.Vanilla);

        // Initialise Other Data
        MinecraftVersionConverter.init();

        // Test Authentication
        //MinePackerRuntime.s_Instance.getAuthenticationManager().attemptMicrosoftLogin();

        // Generate Test Instance
        //MinePackerRuntime.s_Instance.getInstanceManager().generateInstance(MinePackerRuntime.s_Instance.getCurrentProject());
        //MinePackerRuntime.s_Instance.getInstanceManager().loadInstance(MinePackerRuntime.s_Instance.getCurrentProject());
    }

    @Override
    public void start(Stage stage) {
        // Initialise Stage
        stage.setTitle("MinePacker");
        stage.setResizable(false);

        // Center page when size changes
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
        {
            Rectangle2D bounds = Screen.getPrimary().getBounds();
            double x = (bounds.getMaxX() / 2.0) - (stage.getWidth() / 2.0);
            double y = (bounds.getMaxY() / 2.0) - (stage.getHeight() / 2.0);
            stage.setX(x);
            stage.setY(y);
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);


        // Initialise Page Switcher
        new PageSwitcher(stage);
        PageSwitcher.s_Instance.registerPage("project_selection", new ProjectSelectionPage());
        PageSwitcher.s_Instance.registerPage("project", new ProjectPage());
    }

    @Override
    public void stop() {
        MinePackerRuntime.s_Instance.getAuthenticationManager().endAuthServer();

        Serializer.save(MinePackerRuntime.s_Instance.getSettings());
        Serializer.save(MinePackerRuntime.s_Instance.getCurrentProject());
        MinePackerRuntime.s_Instance.getInstanceManager().saveInstances();

        Analytics.endAll();
        if(Analytics.shouldSave()) {
            Serializer.save(new PerformanceProfile());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}