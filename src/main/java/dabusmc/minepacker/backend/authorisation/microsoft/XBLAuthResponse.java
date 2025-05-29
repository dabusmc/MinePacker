package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XBLAuthResponse {

    public Date IssueInstant;
    public Date NotAfter;
    public String Token;
    public XBLDisplayClaims DisplayClaims;

    public static XBLAuthResponse generateFromJSONString(String jsonString) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(jsonString);

            XBLAuthResponse response = new XBLAuthResponse();

            response.Token = obj.get("Token").toString();

            JSONArray xuiArray = (JSONArray) ((JSONObject) obj.get("DisplayClaims")).get("xui");
            List<XBLXUIClaim> xui = new ArrayList<>();

            for(Object xuiClaimObj : xuiArray) {
                JSONObject xuiClaimJSON = (JSONObject) xuiClaimObj;

                XBLXUIClaim xuiClaim = new XBLXUIClaim();
                xuiClaim.UHS = xuiClaimJSON.get("uhs").toString();
                xui.add(xuiClaim);
            }

            response.DisplayClaims = new XBLDisplayClaims();
            response.DisplayClaims.XUI = xui;

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);

            response.IssueInstant = format.parse(obj.get("IssueInstant").toString());
            response.NotAfter = format.parse(obj.get("NotAfter").toString());

            return response;
        } catch (ParseException | java.text.ParseException e) {
            Logger.fatal("OAuthTokenResponse", e.toString());
        }

        return null;
    }

}
