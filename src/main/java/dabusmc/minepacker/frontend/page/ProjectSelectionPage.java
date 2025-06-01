package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.authorisation.microsoft.MicrosoftAccount;
import dabusmc.minepacker.frontend.base.Page;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProjectSelectionPage extends Page {

    private StackPane m_Root;

    public ProjectSelectionPage() {
        m_Root = new StackPane();
    }

    @Override
    public void initComponents() {
        Label test = new Label("Welcome to the Project Selection Page!");

        m_Root.getChildren().add(test);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public int getDimensionMultiplier() {
        return 60;
    }
}
