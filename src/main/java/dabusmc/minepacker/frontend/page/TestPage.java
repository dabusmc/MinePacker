package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.authorisation.microsoft.MicrosoftAccount;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.frontend.base.Page;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TestPage extends Page {

    private StackPane m_Root;

    public TestPage() {
        m_Root = new StackPane();
    }

    @Override
    public void initComponents() {
        VBox vBox = new VBox(5.0);

        Label username = new Label("Username: ");
        Button test = new Button("Welcome to MinePacker!");

        test.setOnAction(event -> {
            AbstractAccount current = MinePackerRuntime.s_Instance.getAuthenticationManager().getCurrentAccount();
            if(current != null) {
                MicrosoftAccount mcAcc = (MicrosoftAccount) current;
                username.setText("Username: " + mcAcc.AccountProfile.Name);
            } else {
                username.setText("Username: Unknown!");
            }
        });

        vBox.getChildren().addAll(username, test);
        m_Root.getChildren().addAll(vBox);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

}
