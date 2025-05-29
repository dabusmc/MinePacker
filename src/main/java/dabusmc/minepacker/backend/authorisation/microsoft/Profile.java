package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    public String ID;
    public String Name;
    public List<Skin> Skins;
    public List<Cape> Capes;

    public static Profile generateFromJSONString(String jsonString) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(jsonString);

            Profile profile = new Profile();

            profile.ID = obj.get("id").toString();
            profile.Name = obj.get("name").toString();

            List<Skin> skins = new ArrayList<>();
            for(Object skinObj : ((JSONArray)obj.get("skins"))) {
                JSONObject skinJSON = (JSONObject) skinObj;
                Skin skin = new Skin();
                skin.ID = skinJSON.get("id").toString();
                skin.URL = skinJSON.get("url").toString();
                skin.State = skinJSON.get("state").toString();
                skin.Variant = skinJSON.get("variant").toString();
                skin.TextureKey = skinJSON.get("textureKey").toString();
                skins.add(skin);
            }

            profile.Skins = skins;

            List<Cape> capes = new ArrayList<>();
            for(Object capeObj : ((JSONArray)obj.get("capes"))) {
                JSONObject capeJSON = (JSONObject) capeObj;
                Cape cape = new Cape();
                cape.ID = capeJSON.get("id").toString();
                capes.add(cape);
            }

            profile.Capes = capes;

            return profile;
        } catch (ParseException e) {
            Logger.fatal("Profile", e.toString());
        }

        return null;
    }

}
