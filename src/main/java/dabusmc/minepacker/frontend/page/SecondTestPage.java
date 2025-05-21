package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.frontend.base.Page;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class SecondTestPage extends Page {

    private StackPane m_Root;

    public SecondTestPage() {
        m_Root = new StackPane();
    }

    @Override
    public void initComponents() {
        Button test = new Button("This is page 2!");

        test.setOnAction(event -> {
            PageSwitcher.s_Instance.switchToPage(PageSwitcher.s_Instance.getPageIndex("test"));
        });

        m_Root.getChildren().add(test);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

}
