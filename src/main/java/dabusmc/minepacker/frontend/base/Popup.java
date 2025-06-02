package dabusmc.minepacker.frontend.base;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class Popup extends Page {

    protected Stage m_Stage;
    protected Supplier<Integer> m_OnClose;

    public Popup() {
        this("");
    }

    public Popup(String name) {
        m_Stage = new Stage();
        m_Stage.initModality(Modality.APPLICATION_MODAL);
        m_Stage.initOwner(PageSwitcher.s_Instance.getStage());
        m_Stage.setTitle(name);
        m_Stage.setResizable(false);
        m_Stage.setOnHiding(event -> finish());
    }

    public void finish() {
        if (m_OnClose != null)
        {
            m_OnClose.get();
        }

        m_Stage.hide();
    }

    public void display() {
        Scene scene = new Scene(getRoot(), getWidth(), getHeight());
        m_Stage.setScene(scene);
        initComponents();
        setScene(scene);
        m_Stage.showAndWait();
    }

    public void setOnClose(Supplier<Integer> onClose) {
        m_OnClose = onClose;
    }

    @Override
    public int getDimensionMultiplier() {
        return 20;
    }

    @Override
    public ScreenRatio getDimensionRatio() {
        return ScreenRatio.Sixteen_Nine;
    }
}
