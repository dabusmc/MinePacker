package dabusmc.minepacker.backend.io.serialization;

import dabusmc.minepacker.backend.analytics.Analytics;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.Consumer;

public class Serializer {

    private static AutoSerializer s_Autosaver = null;

    public static void save(ISaveable saveable) {
        PackerFile.createFolderIfNotExist(saveable.getSaveDirectory());
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        saveToPath(path, saveable);
    }

    public static void save(ISaveableMT saveable, boolean useProgressBar, Consumer<JSONObject> onFinish) {
        if(useProgressBar) {
            ThreadedFunctionWithProgressBar<JSONObject> func = new ThreadedFunctionWithProgressBar<>(saveable.getSaveProcess());
            func.addOnFinish((data) -> {
                PackerFile.createFolderIfNotExist(saveable.getSaveDirectory());
                String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
                saveData(path, data);
            });

            if(onFinish != null) {
                func.addOnFinish(onFinish);
            }

            func.begin();
        } else {
            Task<JSONObject> task = saveable.getSaveProcess();
            task.setOnFailed(wse -> wse.getSource().getException().printStackTrace());
            task.setOnSucceeded((wse) -> {
                JSONObject data = (JSONObject) wse.getSource().getValue();
                PackerFile.createFolderIfNotExist(saveable.getSaveDirectory());
                String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
                saveData(path, data);
            });
            new Thread(task).start();
        }
    }

    public static void saveToPath(String path, ISaveable saveable) {
        JSONObject data = saveable.getSavableObject();
        saveData(path, data);
    }

    private static void saveData(String path, JSONObject data) {
        Analytics.begin("SaveData");

        PackerFile file = new PackerFile(path, true);

        if(!file.fileExists()) {
            file.construct();
        }

        BufferedWriter writer = file.getWriter();
        try {
            writer.write(data.toJSONString());
        } catch (IOException e) {
            Logger.error("Serializer", e.toString());
        }

        file.cleanup();

        Logger.info("Serializer", "Saved JSON Data to path '" + path + "'");

        Analytics.end("SaveData");
    }

    public static void load(ISaveable saveable) {
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        loadFromPath(path, saveable);
    }

    public static void load(ISaveableMT saveable, boolean useProgressBar, Consumer<Void> onFinish) {
        String path = PackerFile.combineFilePaths(saveable.getSaveDirectory(), saveable.getFileName());
        loadFromPath(path, saveable, useProgressBar, onFinish);
    }

    public static void loadFromPath(String path, ISaveable saveable) {
        Analytics.begin("LoadFromPath");

        JSONObject data = loadData(path);

        if(data != null) {
            saveable.getLoadedData(data);
        }

        Analytics.end("LoadFromPath");
    }

    public static void loadFromPath(String path, ISaveableMT saveable, boolean useProgressBar, Consumer<Void> onFinish) {
        // NOTE: It's okay to leave actually reading the data out of the thread as what takes the most time is interpreting the data
        JSONObject data = loadData(path);

        if(useProgressBar) {
            ThreadedFunctionWithProgressBar<Void> func = new ThreadedFunctionWithProgressBar<>(saveable.getLoadProcess(data));

            if(onFinish != null) {
                func.addOnFinish(onFinish);
            }

            func.begin();
        } else {
            Task<Void> task = saveable.getLoadProcess(data);
            task.setOnFailed(wse -> wse.getSource().getException().printStackTrace());
            new Thread(task).start();
        }
    }

    private static JSONObject loadData(String path) {
        PackerFile file = new PackerFile(path, false);

        if(!file.fileExists()) {
            Logger.error("Serializer", "File at path '" + path + "' can't be loaded: File not Found!");
            return null;
        }

        JSONObject data = file.readIntoJson();
        file.cleanup();
        return data;
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
