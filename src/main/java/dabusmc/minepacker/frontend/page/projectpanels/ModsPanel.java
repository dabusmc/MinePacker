package dabusmc.minepacker.frontend.page.projectpanels;

import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ModsPanel extends Panel {

    private MPVBox m_Root;

    public ModsPanel(float screenSpaceRatio, Orientation direction) {
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

        addTitle("Mods");

        MPSeparator titleSeparator = new MPSeparator();
        titleSeparator.setOrientation(Orientation.HORIZONTAL);

        m_Root.getChildren().addAll(titleSeparator);
    }
}
