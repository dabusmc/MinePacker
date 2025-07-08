package dabusmc.minepacker.frontend.base;

import dabusmc.minepacker.backend.logging.Logger;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class Panel extends AbstractComponentList {

    private float m_Width;
    private float m_Height;

    private float m_ScreenRatio;
    private Orientation m_CutDirection;

    private Page m_Page;

    public Panel(float screenSpaceRatio, Orientation direction) {
        m_ScreenRatio = screenSpaceRatio;
        m_CutDirection = direction;
    }

    public void setPage(Page page) {
        m_Page = page;

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

    public void setHeight(float height)
    {
        m_Height = height;
        getRoot().setMaxHeight(m_Height);
        getRoot().setMinHeight(m_Height);
        getRoot().setPrefHeight(m_Height);
    }

    protected void addTitle(String text) {
        Label title = new Label(text);
        title.setFont(new Font("System", 24));
        title.setAlignment(Pos.TOP_CENTER);
        getRoot().getChildren().add(title);
    }

    protected void addCard(Card card) {
        card.reload();
        getRoot().getChildren().add(card.getRoot());
    }

    public float getWidth() {
        return m_Width;
    }

    public float getHeight() {
        return m_Height;
    }

    protected Page getPage() {
        return m_Page;
    }

}
