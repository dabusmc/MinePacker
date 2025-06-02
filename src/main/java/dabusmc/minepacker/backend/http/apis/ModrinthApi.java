package dabusmc.minepacker.backend.http.apis;

import dabusmc.minepacker.MinePackerApp;
import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.http.ModApi;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

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
    public JSONObject search(String query, int pageIndex, int pageLimit) {
        //String extension = "search?query=\"" + query + "\"&facets=" + facets;

        String facets = generateFacets();
        String encodedQuery = URLEncoder.encode("\"" + (query == null ? "" : query) + "\"", StandardCharsets.UTF_8);
        String encodedOffset = URLEncoder.encode(Integer.toString(pageIndex * pageLimit), StandardCharsets.UTF_8);
        String encodedLimit = URLEncoder.encode(Integer.toString(pageLimit), StandardCharsets.UTF_8);

        String extension = "search?query=" + encodedQuery + "&facets=" + facets + "&offset=" + encodedOffset + "&limit=" + encodedLimit;

        HttpResponse<String> response = get(getFinalURL(BASE_URL, extension), false);
        return convertHttpResponseToJSONObject(response);
    }

    @Override
    public String generateFacets() {
        Project currentProject = MinePackerRuntime.s_Instance.getCurrentProject();

        String facets = "[[\"categories:";
        facets += currentProject.getLoader().toString().toLowerCase();
        facets += "\"],[\"versions:";
        facets += currentProject.getMinecraftVersion().toString();
        facets += "\"],[\"project_type:mod\"]]";

        return URLEncoder.encode(facets, StandardCharsets.UTF_8);
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
