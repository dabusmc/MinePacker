package dabusmc.minepacker.frontend.page.projectpanels;

import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class OverviewPanel extends Panel {

    private MPVBox m_Root;

    public OverviewPanel(float screenSpaceRatio, Orientation direction) {
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

        addTitle("Overview");

        MPSeparator titleSeparator = new MPSeparator();
        titleSeparator.setOrientation(Orientation.HORIZONTAL);

        VBox projectDetails = generateProjectDetails();

        // TODO: Add spacing so that the bottom panel is at the bottom of the window

        HBox bottomPanel = generateBottomPanel();

        m_Root.getChildren().addAll(titleSeparator, projectDetails, bottomPanel);
    }

    private VBox generateProjectDetails() {
        return new VBox(5.0);
    }

    private HBox generateBottomPanel() {
        return new HBox(5.0);
    }
}
