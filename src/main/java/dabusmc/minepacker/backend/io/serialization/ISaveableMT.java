package dabusmc.minepacker.backend.io.serialization;

import javafx.concurrent.Task;
import org.json.simple.JSONObject;

public interface ISaveableMT {

    String getFileName();
    String getSaveDirectory();
    Task<JSONObject> getSaveProcess();
    Task<Void> getLoadProcess(JSONObject data);

}
