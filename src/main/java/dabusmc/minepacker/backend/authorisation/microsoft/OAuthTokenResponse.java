package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Date;

public class OAuthTokenResponse {

    public String TokenType;
    public Integer ExpiresIn;
    public Date ExpiresAt;
    public String Scope;
    public String AccessToken;
    public String RefreshToken;
    public String UserID;
    public String Foci;

    public static OAuthTokenResponse generateFromJSONString(String jsonString) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(jsonString);

            OAuthTokenResponse response = new OAuthTokenResponse();

            response.AccessToken = obj.get("access_token").toString();
            response.RefreshToken = obj.get("refresh_token").toString();
            response.Scope = obj.get("scope").toString();
            response.TokenType = obj.get("token_type").toString();
            response.ExpiresIn = Integer.valueOf(obj.get("expires_in").toString());

            response.ExpiresAt = new Date();
            response.ExpiresAt.setTime(response.ExpiresAt.getTime() + (response.ExpiresIn * 1000));

            if (obj.containsKey("user_id")) {
                response.UserID = obj.get("user_id").toString();
            }

            if (obj.containsKey("foci")) {
                response.Foci = obj.get("foci").toString();
            }

            return response;
        } catch (ParseException e) {
            Logger.fatal("OAuthTokenResponse", e.toString());
        }

        return null;
    }

}
