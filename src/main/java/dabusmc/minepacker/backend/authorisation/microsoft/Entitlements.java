package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Entitlements {

    public List<EntitlementItem> Items;
    public String Signature;

    public static Entitlements generateFromJSONString(String jsonString) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(jsonString);

            Entitlements entitlements = new Entitlements();

            entitlements.Signature = obj.get("signature").toString();

            List<EntitlementItem> entitlementItems = new ArrayList<>();
            JSONArray itemsJSON = (JSONArray) obj.get("items");
            for(Object itemObj : itemsJSON) {
                JSONObject itemJSON = (JSONObject) itemObj;
                EntitlementItem item = new EntitlementItem();
                item.Name = itemJSON.get("name").toString();
                item.Source = itemJSON.get("source").toString();
                entitlementItems.add(item);
            }

            entitlements.Items = entitlementItems;

            return entitlements;
        } catch (ParseException e) {
            Logger.fatal("Entitlements", e.toString());
        }

        return null;
    }

}
