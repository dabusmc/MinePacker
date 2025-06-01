package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.frontend.base.Page;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class ProjectPage extends Page {

    private StackPane m_Root;

    public ProjectPage() {
        m_Root = new StackPane();
    }

    @Override
    public void initComponents() {
        Label test = new Label("Welcome to the Project Page!");

        m_Root.getChildren().add(test);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

}
