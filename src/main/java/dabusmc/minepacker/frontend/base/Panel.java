package dabusmc.minepacker.frontend.base;

import dabusmc.minepacker.backend.logging.Logger;
import javafx.geometry.Orientation;

public class Panel extends AbstractComponentList {

    protected float m_Width;
    protected float m_Height;

    private float m_ScreenRatio;
    private Orientation m_CutDirection;

    public Panel(float screenSpaceRatio, Orientation direction) {
        m_ScreenRatio = screenSpaceRatio;
        m_CutDirection = direction;
    }

    public void setPage(Page page) {
        m_Width = ScreenRatioHelper.getWidth(page.getDimensionRatio()) * page.getDimensionMultiplier();
        m_Height = ScreenRatioHelper.getHeight(page.getDimensionRatio()) * page.getDimensionMultiplier();

        if(m_CutDirection == Orientation.HORIZONTAL) {
            m_Width *= m_ScreenRatio;
        } else {
            m_Height *= m_ScreenRatio;
        }

        getRoot().setMaxWidth(m_Width);
        getRoot().setMinWidth(m_Width);
        getRoot().setPrefWidth(m_Width);
        getRoot().setMaxHeight(m_Height);
        getRoot().setMinHeight(m_Height);
        getRoot().setPrefHeight(m_Height);
    }

}
