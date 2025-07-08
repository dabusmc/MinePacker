package dabusmc.minepacker.frontend.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MPButtonImage extends MPButton {

    public MPButtonImage() {
        super();
    }

    public MPButtonImage(Image image) {
        super();
        setImage(image, image.getWidth(), image.getHeight());
    }

    public MPButtonImage(Image image, double width, double height) {
        super();
        setImage(image, width, height);
    }

    public void setImage(Image image, double width, double height) {
        ImageView view = new ImageView(image);
        view.setFitWidth(width);
        view.setFitHeight(height);
        setGraphic(view);
    }


}
