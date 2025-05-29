package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginResponse {

    public String Username;
    public String AccessToken;
    public String TokenType;
    public Integer ExpiresIn;

    public static LoginResponse generateFromJSONString(String jsonString) {
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(jsonString);

            LoginResponse response = new LoginResponse();

            for(Object key : obj.keySet()) {
                Logger.info("LoginResponse", key);
            }

            return null;
        } catch (ParseException e) {
            Logger.fatal("LoginResponse", e.toString());
        }

        return null;
    }

}
