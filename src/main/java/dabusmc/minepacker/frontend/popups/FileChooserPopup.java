package dabusmc.minepacker.frontend.popups;

import dabusmc.minepacker.frontend.base.PageSwitcher;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

public class FileChooserPopup {

    public static File chooseFile(String directory, String title, FileChooser.ExtensionFilter filter)
    {
        FileChooser chooser = new FileChooser();
        if (!directory.isEmpty())
        {
            chooser.setInitialDirectory(new File(directory));
        }
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(filter);
        return chooser.showOpenDialog(PageSwitcher.s_Instance.getStage());
    }

    public static File chooseFile(String title, FileChooser.ExtensionFilter filter)
    {
        return chooseFile("", title, filter);
    }

}
