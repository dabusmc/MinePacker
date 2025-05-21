package dabusmc.minepacker.frontend.base;

public class ScreenRatioHelper {

    public static int getWidth(ScreenRatio ratio) {
        switch (ratio) {
            case Sixteen_Nine, Sixteen_Ten -> {
                return 16;
            }
            case Nine_Sixteen -> {
                return 9;
            }
            case Ten_Sixteen, One_One -> {
                return 10;
            }
        }

        return 0;
    }

    public static int getHeight(ScreenRatio ratio) {
        switch (ratio) {
            case Sixteen_Nine -> {
                return 9;
            }
            case Sixteen_Ten, One_One -> {
                return 10;
            }
            case Nine_Sixteen, Ten_Sixteen -> {
                return 16;
            }
        }

        return 0;
    }

}
