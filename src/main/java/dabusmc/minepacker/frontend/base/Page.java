package dabusmc.minepacker.frontend.base;

import javafx.scene.Scene;

public class Page extends AbstractComponentList {

    protected Scene m_Scene;

    public Scene getScene() {
        return m_Scene;
    }

    public void setScene(Scene scene) {
        m_Scene = scene;
    }

    public void onClose() {

    }

    public ScreenRatio getDimensionRatio() {
        return ScreenRatio.Sixteen_Ten;
    }

    public int getDimensionMultiplier() {
        return 80;
    }

    public double getWidth() {
        return ScreenRatioHelper.getWidth(getDimensionRatio()) * getDimensionMultiplier();
    }

    public double getHeight() {
        return ScreenRatioHelper.getHeight(getDimensionRatio()) * getDimensionMultiplier();
    }

    public void addPanel(Panel panel) {
        panel.setPage(this);
        panel.reload();
        getRoot().getChildren().addAll(panel.getRoot());
    }

}
