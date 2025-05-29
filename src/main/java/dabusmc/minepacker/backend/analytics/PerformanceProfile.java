package dabusmc.minepacker.backend.analytics;

import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.io.serialization.ISaveable;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class PerformanceProfile implements ISaveable {

    @Override
    public String getFileName() {
        return "analytics.json";
    }

    @Override
    public String getSaveDirectory() {
        return PackerFile.combineFilePaths(PackerFile.getCWD(), "runtime");
    }

    @Override
    public JSONObject getSavableObject() {
        HashMap<String, Long> profile = Analytics.getPerformanceMap();

        JSONObject obj = new JSONObject();

        for(String name : profile.keySet()) {
            obj.put(name, profile.get(name));
        }

        return obj;
    }

    @Override
    public void getLoadedData(JSONObject data) {

    }

}
