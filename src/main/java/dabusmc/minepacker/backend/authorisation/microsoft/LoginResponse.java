package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Date;

public class LoginResponse {

    public String Username;
    public String AccessToken;
    public String TokenType;
    public Integer ExpiresIn;
    public Date ExpiresAt;

    public static LoginResponse generateFromJSONString(String jsonString) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(jsonString);

            LoginResponse response = new LoginResponse();

            response.Username = obj.get("username").toString();
            response.AccessToken = obj.get("access_token").toString();
            response.TokenType = obj.get("token_type").toString();
            response.ExpiresIn = Integer.valueOf(obj.get("expires_in").toString());

            response.ExpiresAt = new Date();
            response.ExpiresAt.setTime(response.ExpiresAt.getTime() + (response.ExpiresIn * 1000));

            return response;
        } catch (ParseException e) {
            Logger.fatal("LoginResponse", e.toString());
        }

        return null;
    }

}
