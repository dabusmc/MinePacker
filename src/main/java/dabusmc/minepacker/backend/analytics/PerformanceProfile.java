package dabusmc.minepacker.backend.analytics;

import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.io.serialization.ISaveable;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

public class PerformanceProfile implements ISaveable {

    @Override
    public String getFileName() {
        return "performance_profile_" + UUID.randomUUID() + ".json";
    }

    @Override
    public String getSaveDirectory() {
        return PackerFile.combineFilePaths(PackerFile.getCWD(), "runtime/analytics");
    }

    @Override
    public JSONObject getSavableObject() {
        HashMap<String, AnalyticProfileCollection> performanceMap = Analytics.getPerformanceMap();

        JSONObject obj = new JSONObject();

        for(String name : performanceMap.keySet()) {
            AnalyticProfileCollection profileCollection = performanceMap.get(name);

            long average = profileCollection.AverageTime / profileCollection.Profiles.size();

            JSONObject collectionJSON = new JSONObject();
            collectionJSON.put("average_ms", average * 1.0);
            collectionJSON.put("average_s", average / 1000.0);

            JSONArray profilesJSON = new JSONArray();
            for(AnalyticProfile profile : profileCollection.Profiles) {
                JSONObject profileJSON = new JSONObject();

                profileJSON.put("id", profile.Name);
                profileJSON.put("time_taken_ms", profile.TimeTakenMs * 1.0);
                profileJSON.put("time_taken_s", profile.TimeTakenMs / 1000.0);

                profilesJSON.add(profileJSON);
            }
            collectionJSON.put("profiles", profilesJSON);

            obj.put(name, collectionJSON);
        }

        return obj;
    }

    @Override
    public void getLoadedData(JSONObject data) {

    }

}
