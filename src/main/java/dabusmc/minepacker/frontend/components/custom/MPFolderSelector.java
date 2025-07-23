package dabusmc.minepacker.frontend.components.custom;

import dabusmc.minepacker.frontend.base.PageSwitcher;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPHBox;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MPFolderSelector extends MPHBox {

    public MPFolderSelector(String title,
                            String currentVar,
                            Consumer<String> varSet,
                            Supplier<Integer> reloadFunction,
                            String initialDirectoryPath) {
        super(5.0);
        setAlignment(Pos.CENTER);

        Label folderSelection = new Label("None selected...");
        folderSelection.setTextAlignment(TextAlignment.CENTER);
        if(!currentVar.isEmpty()) {
            folderSelection.setText(currentVar);
        }

        MPButton selectFolder = new MPButton("...");
        selectFolder.setOnAction(action -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle(title);

            if(!initialDirectoryPath.isEmpty()) {
                chooser.setInitialDirectory(new File(initialDirectoryPath).getParentFile());
            }

            File selectedDir = chooser.showDialog(PageSwitcher.s_Instance.getStage());

            if(selectedDir != null) {
                varSet.accept(selectedDir.getAbsolutePath());
                reloadFunction.get();
            }
        });

        getChildren().addAll(folderSelection, selectFolder);
    }

}
