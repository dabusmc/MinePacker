package dabusmc.minepacker.backend.util;

import dabusmc.minepacker.backend.logging.Logger;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

public class ImageUtils {

    public static boolean isWebp(String url) {
        return url.toLowerCase().endsWith(".webp");
    }

    public static Image loadWebpImage(String url) {
        if(!isWebp(url)) {
            return null;
        }

        try {
            ImageIO.scanForPlugins(); // Ensures plugin is registered

            InputStream is = new URL(url).openStream();
            BufferedImage bufferedImage = ImageIO.read(is);
            is.close();

            if (bufferedImage == null) {
                Logger.error("ImageUtils", "Failed to decode WebP image: " + url);
                return null;
            }

            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            Logger.error("ImageUtils", "Failed to load image: " + url);
            return null;
        }
    }

}
