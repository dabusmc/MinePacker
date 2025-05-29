package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.authorisation.microsoft.MicrosoftAccount;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.frontend.base.Page;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class TestPage extends Page {

    private StackPane m_Root;

    public TestPage() {
        m_Root = new StackPane();
    }

    @Override
    public void initComponents() {
        Button test = new Button("Welcome to MinePacker!");

        test.setOnAction(event -> {
            AbstractAccount current = MinePackerRuntime.s_Instance.getAuthenticationManager().getWorkingAccount();
            if(current != null) {
                MicrosoftAccount mcAcc = (MicrosoftAccount) current;
                Logger.info("TestPage", "Microsoft Login Successful: " + mcAcc.AccessToken);
            } else {
                Logger.error("TestPage", "Microsoft Login Failed!");
            }
        });

        m_Root.getChildren().add(test);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

}
