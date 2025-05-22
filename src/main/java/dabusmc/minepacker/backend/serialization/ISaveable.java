package dabusmc.minepacker.backend.serialization;

import org.json.simple.JSONObject;

public interface ISaveable {

    String getFileName();
    String getSaveDirectory();
    JSONObject getSavableObject();
    void getLoadedData(JSONObject data);

}
