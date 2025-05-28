package dabusmc.minepacker.backend.io.serialization;

import org.json.simple.JSONObject;

public interface ISaveable {

    String getFileName();
    String getSaveDirectory();
    JSONObject getSavableObject();
    void getLoadedData(JSONObject data);

}
