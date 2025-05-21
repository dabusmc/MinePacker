package dabusmc.minepacker.backend.mod_api.apis;

import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApi;
import dabusmc.minepacker.backend.mod_api.ModApiType;
import org.json.simple.JSONObject;

import java.net.http.HttpResponse;

public class ModrinthApi extends ModApi {

    public static final String BASE_URL = "https://api.modrinth.com/v2";

    @Override
    public boolean modIDExists(String id) {
        HttpResponse<String> response = get(getFinalURL(BASE_URL, "project/" + id + "/check"), false);
        return response.statusCode() == 200;
    }

    @Override
    public Mod getModFromID(String id) {
        if(modIDExists(id)) {
            JSONObject modObj = convertHttpResponseToJSONObject(get(getFinalURL(BASE_URL, "project/" + id), true));
            return constructModFromJsonObject(modObj);
        }

        return null;
    }

    @Override
    public Mod constructModFromJsonObject(JSONObject obj) {
        Mod m = new Mod();

        m.setID(obj.get("id").toString());
        m.setSlug(obj.get("slug").toString());
        m.setTitle(obj.get("title").toString());
        m.setTagline(obj.get("description").toString());
        m.setDescription(obj.get("body").toString());

        return m;
    }
}
