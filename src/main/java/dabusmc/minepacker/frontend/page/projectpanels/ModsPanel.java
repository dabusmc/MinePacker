package dabusmc.minepacker.frontend.page.projectpanels;

import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.popups.modspanel.AddModsPopup;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

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

        MPVBox modView = generateModView();

        MPSeparator bottomPanelSeparator = new MPSeparator();
        bottomPanelSeparator.setOrientation(Orientation.HORIZONTAL);

        MPVBox bottomPanel = generateBottomPanel();

        Region spacer = new Region();
        MPVBox.setVgrow(spacer, Priority.NEVER);
        spacer.setMinHeight(getHeight() - (getHeight() * 0.2875));

        m_Root.getChildren().addAll(titleSeparator, modView, spacer, bottomPanelSeparator, bottomPanel);
    }

    private MPVBox generateModView() {
        MPVBox modViewRoot = new MPVBox(7.5);
        modViewRoot.setBoxWidth(getWidth());
        modViewRoot.setAlignment(Pos.TOP_CENTER);
        modViewRoot.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        return modViewRoot;
    }

    private MPVBox generateBottomPanel() {
        MPVBox bottomPanelRoot = new MPVBox(7.5);
        bottomPanelRoot.setBoxWidth(getWidth());
        bottomPanelRoot.setBoxHeight(getHeight() * 0.1);
        bottomPanelRoot.setAlignment(Pos.CENTER);
        bottomPanelRoot.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        MPButton addModsButton = new MPButton("Add Mods");
        addModsButton.setBtnWidth(getWidth() * 0.35);
        addModsButton.setOnAction(action -> addMods());

        bottomPanelRoot.getChildren().add(addModsButton);

        return bottomPanelRoot;
    }

    private void addMods() {
        AddModsPopup popup = new AddModsPopup();
        popup.display();
    }
}
