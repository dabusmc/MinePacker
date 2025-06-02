package dabusmc.minepacker.frontend.base;

public abstract class Card extends AbstractComponentList {

    private double m_Width;
    private double m_Height;

    public Card() {
        m_Width = ScreenRatioHelper.getWidth(ScreenRatio.Sixteen_Ten) * getSizeMultiplier();
        m_Height = ScreenRatioHelper.getHeight(ScreenRatio.Sixteen_Ten) * getSizeMultiplier();
    }

    protected int getSizeMultiplier() {
        return 10;
    }

    protected double getWidth() {
        return m_Width;
    }

    protected double getHeight() {
        return m_Height;
    }

    public void setWidth(double width) {
        m_Width = width;
    }

}
