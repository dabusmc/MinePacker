package dabusmc.minepacker.frontend.threaded;

import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.util.ImageUtils;
import dabusmc.minepacker.frontend.base.PageSwitcher;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageLoaderMT {

    public static ImageLoaderMT Instance;

    private HashMap<String, String> m_ImagesToLoad = new HashMap<>();
    private HashMap<String, Image> m_LoadedImages = new HashMap<>();

    private ScheduledExecutorService m_Executor;

    public ImageLoaderMT() {
        if(Instance != null) {
            Logger.error("ImageLoaderMT", "There should only ever be one instance of ImageLoaderMT");
        } else {
            Instance = this;

            Runnable loadImages = () -> {
                boolean imagesChanged = false;
                for(int i = 0; i < m_ImagesToLoad.size(); i++)
                {
                    String name = (String) m_ImagesToLoad.keySet().toArray()[i];
                    String url = m_ImagesToLoad.get(name);

                    Image image;
                    if(ImageUtils.isWebp(url)) {
                        image = ImageUtils.loadWebpImage(url);
                    } else {
                        image = new Image(url);
                    }

                    m_LoadedImages.put(name, image);
                    m_ImagesToLoad.remove(name);

                    //Logger.message("ImageLoaderMT", "Loaded Image " + name);

                    imagesChanged = true;
                }

                if(imagesChanged)
                {
                    Platform.runLater(() -> PageSwitcher.s_Instance.getCurrentPage().reload());
                }
            };

            m_Executor = Executors.newScheduledThreadPool(2);
            m_Executor.scheduleAtFixedRate(loadImages, 0, 1, TimeUnit.SECONDS);

            Logger.info("ImageLoaderMT", "Image Loader Running");
        }
    }

    public boolean hasImageReady(String name) {
        return m_LoadedImages.containsKey(name);
    }

    public void addImage(String imageName, String url) {
        if(!m_ImagesToLoad.containsKey(imageName)) {
            m_ImagesToLoad.put(imageName, url);
        }
    }

    public Image getImage(String imageName) {
        if(hasImageReady(imageName)) {
            return m_LoadedImages.get(imageName);
        }

        return null;
    }

    public int getLoadedImagesSize() {
        return m_LoadedImages.size();
    }

    public void stop() {
        m_Executor.shutdown();

        Logger.info("ImageLoaderMT", "Image Loader Shutdown");
    }

}
