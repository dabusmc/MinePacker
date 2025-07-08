package dabusmc.minepacker.backend.io;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.util.OSUtils;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Browser {

    public static void open(String url) {
        try {
            open(new URI(url));
        } catch (URISyntaxException e) {
            Logger.fatal("Browser", e.toString());
        }
    }

    public static void open(URI uri) {
        try {
            if (MinePackerRuntime.Instance.getOS() == MinePackerRuntime.OS.Linux && OSUtils.executableInPath("xdg-open")) {
                Runtime.getRuntime().exec("xdg-open " + uri);
            } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            } else {
                Logger.error("Browser", "Cannot open web browser as no supported methods were found");
            }
        } catch (Exception e) {
            Logger.fatal("Browser", e.toString());
        }
    }

}
