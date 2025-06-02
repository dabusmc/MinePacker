package dabusmc.minepacker.frontend.page.projectpanels;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.io.serialization.Serializer;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPHBox;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.popups.NotImplementedPopup;
import dabusmc.minepacker.frontend.popups.TextPopup;
import dabusmc.minepacker.frontend.popups.YesNoPopup;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

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

        MPVBox projectDetails = generateProjectDetails();

        MPSeparator bottomPanelSeparator = new MPSeparator();
        bottomPanelSeparator.setOrientation(Orientation.HORIZONTAL);

        MPVBox bottomPanel = generateBottomPanel();

        Region spacer = new Region();
        MPVBox.setVgrow(spacer, Priority.NEVER);
        spacer.setMinHeight(getHeight() - (getHeight() * 0.2875));

        m_Root.getChildren().addAll(titleSeparator, projectDetails, spacer, bottomPanelSeparator, bottomPanel);
    }

    private MPVBox generateProjectDetails() {
        MPVBox projectDetailsRoot = new MPVBox(7.5);
        projectDetailsRoot.setBoxWidth(getWidth());
        projectDetailsRoot.setAlignment(Pos.TOP_CENTER);
        projectDetailsRoot.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        return projectDetailsRoot;
    }

    private MPVBox generateBottomPanel() {
        MPVBox bottomPanelRoot = new MPVBox(7.5);
        bottomPanelRoot.setBoxWidth(getWidth());
        bottomPanelRoot.setAlignment(Pos.TOP_CENTER);
        bottomPanelRoot.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        double buttonSize = getWidth() / 4.0;

        // Generate First Button Row
        MPHBox rowOne = new MPHBox(7.5);
        rowOne.setBoxWidth(getWidth());
        rowOne.setAlignment(Pos.TOP_CENTER);

        MPButton editButton = new MPButton("Edit");
        editButton.setBtnWidth(buttonSize);
        editButton.setOnAction(action -> editProject());

        MPButton exportButton = new MPButton("Export");
        exportButton.setBtnWidth(buttonSize);
        exportButton.setOnAction(action -> exportProject());

        rowOne.getChildren().addAll(editButton, exportButton);

        // Generate Second Button Row
        MPHBox rowTwo = new MPHBox(7.5);
        rowTwo.setBoxWidth(getWidth());
        rowTwo.setAlignment(Pos.TOP_CENTER);

        MPButton saveButton = new MPButton("Save");
        saveButton.setBtnWidth(buttonSize);
        saveButton.setOnAction(action -> saveProject());

        MPButton closeButton = new MPButton("Close");
        closeButton.setBtnWidth(buttonSize);
        closeButton.setOnAction(action -> closeProject());

        rowTwo.getChildren().addAll(saveButton, closeButton);

        bottomPanelRoot.getChildren().addAll(rowOne, rowTwo);

        return bottomPanelRoot;
    }

    private void editProject() {
        NotImplementedPopup popup = new NotImplementedPopup();
        popup.display();
    }

    private void exportProject() {
        NotImplementedPopup popup = new NotImplementedPopup();
        popup.display();
    }

    private void saveProject() {
        Serializer.save(MinePackerRuntime.s_Instance.getCurrentProject());
        MinePackerRuntime.s_Instance.getCurrentProject().saved();
        TextPopup popup = new TextPopup("Project has been Saved");
        popup.display();
    }

    private void closeProject() {
        if(MinePackerRuntime.s_Instance.getCurrentProject().shouldSave()) {
            YesNoPopup popup = new YesNoPopup("Do you want to save the project?");
            popup.display();

            if(popup.getAnswer()) {
                saveProject();
            }
        }

        PageSwitcher.s_Instance.reset();
        Logger.info("OverviewPanel", "Closed Project: '" + MinePackerRuntime.s_Instance.getCurrentProject().getName() + "'");
    }

}
