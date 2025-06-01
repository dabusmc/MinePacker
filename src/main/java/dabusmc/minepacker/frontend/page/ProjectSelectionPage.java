package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.authorisation.microsoft.MicrosoftAccount;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.io.serialization.Serializer;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.frontend.base.Page;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.popups.FileChooserPopup;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

public class ProjectSelectionPage extends Page {

    private HBox m_Root;

    public ProjectSelectionPage() {
        m_Root = new HBox(5.0);
    }

    @Override
    public void initComponents() {
        addPanel(new PSPLeftPanel(0.25f, Orientation.HORIZONTAL));

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        m_Root.getChildren().addAll(separator);

        addPanel(new PSPRightPanel(0.75f, Orientation.HORIZONTAL));
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public int getDimensionMultiplier() {
        return 60;
    }

    public class PSPLeftPanel extends Panel {

        private MPVBox m_Root;

        public PSPLeftPanel(float screenSpaceRatio, Orientation direction) {
            super(screenSpaceRatio, direction);

            m_Root = new MPVBox(7.5);
        }

        @Override
        public Pane getRoot() {
            return m_Root;
        }

        @Override
        public void initComponents() {
            m_Root.setBoxWidth(m_Width);
            m_Root.setAlignment(Pos.TOP_CENTER);
            m_Root.setPadding(new Insets(7.5f));

            MPButton newProjectButton = new MPButton("New Project...");
            newProjectButton.setBtnWidth(m_Width * 0.75f);

            newProjectButton.setOnAction(action -> {
                Logger.info("ProjectSelectionPage", "TODO: Create New Project Dialogue");
            });

            MPButton openProjectButton = new MPButton("Open Project...");
            openProjectButton.setBtnWidth(m_Width * 0.75f);

            openProjectButton.setOnAction(action -> {
                File projectFile;

                if (!MinePackerRuntime.s_Instance.getSettings().getProjectsDir().isEmpty()) {
                    projectFile = FileChooserPopup.chooseFile(MinePackerRuntime.s_Instance.getSettings().getProjectsDir(),
                            "Project File",
                            new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                } else {
                    projectFile = FileChooserPopup.chooseFile("Project File",
                            new FileChooser.ExtensionFilter("JSON Files", "*.json"));
                }

                if (projectFile != null) {
                    Project prj = new Project();
                    Serializer.loadFromPath(projectFile.getAbsolutePath(), prj);
                    MinePackerRuntime.s_Instance.setCurrentProject(prj);
                    Logger.info("ProjectSelectionPage", "Opened Project: '" + prj.getName() + "'");
                    PageSwitcher.s_Instance.switchToPage(PageSwitcher.s_Instance.getPageIndex("project"));
                }
            });

            m_Root.getChildren().addAll(newProjectButton, openProjectButton);
        }
    }

    public class PSPRightPanel extends Panel {

        private MPVBox m_Root;

        public PSPRightPanel(float screenSpaceRatio, Orientation direction) {
            super(screenSpaceRatio, direction);

            m_Root = new MPVBox(7.5);
        }

        @Override
        public Pane getRoot() {
            return m_Root;
        }

        @Override
        public void initComponents() {
            m_Root.setBoxWidth(m_Width);
            m_Root.setAlignment(Pos.TOP_LEFT);
            m_Root.setPadding(new Insets(7.5f));

            Label noRecentProjects = new Label("No recent projects...");

            m_Root.getChildren().add(noRecentProjects);
        }
    }

}
