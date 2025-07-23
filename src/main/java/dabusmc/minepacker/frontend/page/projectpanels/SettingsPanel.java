package dabusmc.minepacker.frontend.page.projectpanels;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.util.StringUtils;
import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.components.custom.MPFolderSelector;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingsPanel extends Panel {

    private MPVBox m_Root;

    public SettingsPanel(float screenSpaceRatio, Orientation direction) {
        super(screenSpaceRatio, direction);
        m_Root = new MPVBox(5.0);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        m_Root.setBoxWidth(getWidth());
        m_Root.setAlignment(Pos.TOP_CENTER);
        m_Root.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        addTitle("Settings");

        MPSeparator titleSeparator = new MPSeparator();
        titleSeparator.setOrientation(Orientation.HORIZONTAL);

        Label projectFolderTitle = new Label("Default Projects Folder");
        projectFolderTitle.setFont(Font.font("System", FontWeight.BOLD, 13));

        MPFolderSelector projectFolderSelector = new MPFolderSelector(
                "Select Default Projects Folder",
                StringUtils.preparePath(MinePackerRuntime.Instance.getSettings().getProjectsDir()),
                (var) -> MinePackerRuntime.Instance.getSettings().setProjectsDir(var),
                () -> { reload(); return 0; },
                MinePackerRuntime.Instance.getSettings().getProjectsDir()
        );

        m_Root.getChildren().addAll(titleSeparator, projectFolderTitle, projectFolderSelector);
    }
}
