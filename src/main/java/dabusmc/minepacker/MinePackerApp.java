package dabusmc.minepacker;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.analytics.Analytics;
import dabusmc.minepacker.backend.analytics.PerformanceProfile;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersionConverter;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.http.ModApiType;
import dabusmc.minepacker.backend.io.serialization.Serializer;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.popups.YesNoPopup;
import dabusmc.minepacker.frontend.threaded.ImageLoaderMT;
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
        MinePackerRuntime.Instance.setLogLevel(LogLevel.MESSAGE);
        MinePackerRuntime.Instance.constructModApi(ModApiType.Modrinth);
        MinePackerRuntime.Instance.setCurrentProject(Project.generateDefaultProject());

        // Initialise Other Data
        MinecraftVersionConverter.init();
        new ImageLoaderMT();

        // Test Authentication
        //MinePackerRuntime.s_Instance.getAuthenticationManager().attemptMicrosoftLogin();

        // Generate Test Instance
        //MinePackerRuntime.s_Instance.getInstanceManager().generateInstance(MinePackerRuntime.s_Instance.getCurrentProject());
        //MinePackerRuntime.s_Instance.getInstanceManager().loadInstance(MinePackerRuntime.s_Instance.getCurrentProject());

        // Begin Autosaver
        Serializer.startAutosaver();
    }

    @Override
    public void start(Stage stage) {
        // Initialise Stage
        stage.setTitle("MinePacker");
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> onCloseRequested());

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
        PageSwitcher.s_Instance.reset();
    }

    @Override
    public void stop() {
        // Finish App
        MinePackerRuntime.Instance.getAuthenticationManager().endAuthServer();

        // Serialization
        MinePackerRuntime.Instance.getInstanceManager().saveInstances();

        // Image Loader
        ImageLoaderMT.Instance.stop();

        Analytics.endAll();
        if(Analytics.shouldSave()) {
            Serializer.save(new PerformanceProfile());
        }

        Serializer.stopAutosaver();
    }

    private void onCloseRequested() {
        // Project
        if(MinePackerRuntime.Instance.getCurrentProject().shouldSave()) {
            YesNoPopup popup = new YesNoPopup("You have unsaved changes. Would you like to save?");
            popup.display();

            if(popup.getAnswer()) {
                Serializer.save(MinePackerRuntime.Instance.getCurrentProject(), true, null);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}