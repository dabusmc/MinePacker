package dabusmc.minepacker.backend.io.serialization;

import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;

public class Serializer {

    public static void save(ISaveable saveable) {
        PackerFile.createFolderIfNotExist(saveable.getSaveDirectory());
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        saveToPath(path, saveable);
    }

    public static void saveToPath(String path, ISaveable saveable) {
        PackerFile file = new PackerFile(path, true);

        if(!file.fileExists()) {
            file.construct();
        }

        JSONObject data = saveable.getSavableObject();

        BufferedWriter writer = file.getWriter();
        try {
            writer.write(data.toJSONString());
        } catch (IOException e) {
            Logger.error("Serializer", e.toString());
        }

        file.cleanup();

        Logger.info("Serializer", "Saved JSON Data to path '" + path + "'");
    }

    public static void load(ISaveable saveable) {
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        loadFromPath(path, saveable);
    }

    public static void loadFromPath(String path, ISaveable saveable) {
        PackerFile file = new PackerFile(path, false);

        if(!file.fileExists()) {
            Logger.error("Serializer", "File at path '" + path + "' can't be loaded: File not Found!");
            file.cleanup();
            return;
        }

        saveable.getLoadedData(file.readIntoJson());
    }

}
