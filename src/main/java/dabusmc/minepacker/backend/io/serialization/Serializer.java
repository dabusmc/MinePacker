package dabusmc.minepacker.backend.io.serialization;

import dabusmc.minepacker.backend.analytics.Analytics;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;

public class Serializer {

    private static AutoSerializer s_Autosaver = null;

    public static void save(ISaveable saveable) {
        PackerFile.createFolderIfNotExist(saveable.getSaveDirectory());
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        saveToPath(path, saveable);
    }

    public static void saveToPath(String path, ISaveable saveable) {
        Analytics.begin("SaveToPath");

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

        Analytics.end("SaveToPath");
    }

    public static void load(ISaveable saveable) {
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        loadFromPath(path, saveable);
    }

    public static void loadFromPath(String path, ISaveable saveable) {
        Analytics.begin("LoadFromPath");

        PackerFile file = new PackerFile(path, false);

        if(!file.fileExists()) {
            Logger.error("Serializer", "File at path '" + path + "' can't be loaded: File not Found!");
            return;
        }

        saveable.getLoadedData(file.readIntoJson());
        file.cleanup();

        Analytics.end("LoadFromPath");
    }

    public static void registerForAutosave(AutoSaveable saveable) {
        if(s_Autosaver == null) {
            s_Autosaver = new AutoSerializer();
        }
        s_Autosaver.register(saveable);
    }

    public static void startAutosaver() {
        s_Autosaver.start();
    }

    public static void stopAutosaver() {
        s_Autosaver.stop();
    }

}
