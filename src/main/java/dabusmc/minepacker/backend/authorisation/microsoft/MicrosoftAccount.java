package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.MinePackerApp;
import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.util.StringUtils;
import org.json.simple.JSONObject;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MicrosoftAccount extends AbstractAccount {

    // NOTE: If you fork or modify this repository, you must not use this Client ID
    public static final String MICROSOFT_LOGIN_CLIENT_ID = "63e9f5bf-7469-4682-9707-17917acc661b";
    public static final int MICROSOFT_LOGIN_REDIRECT_PORT = 29650;
    public static final String MICROSOFT_LOGIN_REDIRECT_URL = "http://127.0.0.1:" + MICROSOFT_LOGIN_REDIRECT_PORT + "/index.html";
    public static final String MICROSOFT_LOGIN_REDIRECT_URL_ENCODED = "http%3A%2F%2F127.0.0.1%3A"
            + MICROSOFT_LOGIN_REDIRECT_PORT + "%2Findex.html";
    public static final String[] MICROSOFT_LOGIN_SCOPES = { "XboxLive.signin", "offline_access" };

    public static final String MICROSOFT_LOGIN_URL = "https://login.live.com/oauth20_authorize.srf" + "?client_id="
            + MICROSOFT_LOGIN_CLIENT_ID
            + "&prompt=select_account&response_type=code" + "&scope="
            + String.join("%20", MICROSOFT_LOGIN_SCOPES) + "&redirect_uri=" + MICROSOFT_LOGIN_REDIRECT_URL_ENCODED;
    public static final String MICROSOFT_DEVICE_CODE_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode";
    public static final String MICROSOFT_AUTH_TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
    public static final String MICROSOFT_XBL_AUTH_TOKEN_URL = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String MICROSOFT_XSTS_AUTH_TOKEN_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MICROSOFT_MINECRAFT_LOGIN_URL = "https://api.minecraftservices.com/launcher/login";
    public static final String MICROSOFT_MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    public static final String MICROSOFT_MINECRAFT_ENTITLEMENTS_URL = "https://api.minecraftservices.com/entitlements/license";



    public static OAuthTokenResponse getAccessTokenFromCode(String code) {
        String urlEncodedArgs = StringUtils.generateURLEncodedString(
                "client_id", MICROSOFT_LOGIN_CLIENT_ID,
                "code", code,
                "grant_type", "authorization_code",
                "redirect_uri", MICROSOFT_LOGIN_REDIRECT_URL,
                "scope", String.join(" ", MICROSOFT_LOGIN_SCOPES)
        );

        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().post(MICROSOFT_AUTH_TOKEN_URL,
                urlEncodedArgs, true, "Content-Type", "application/x-www-form-urlencoded");
        return OAuthTokenResponse.generateFromJSONString(response.body());
    }

    public static XBLAuthResponse getXBLToken(String accessToken) {
        JSONObject properties = new JSONObject();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", "d=" + accessToken);

        JSONObject obj = new JSONObject();
        obj.put("Properties", properties);
        obj.put("RelyingParty", "http://auth.xboxlive.com");
        obj.put("TokenType", "JWT");

        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().post(MICROSOFT_XBL_AUTH_TOKEN_URL,
                obj.toJSONString(), true, "Content-Type", "application/json", "Accept", "application/json", "x-xbl-contract-version",
                "1");
        return XBLAuthResponse.generateFromJSONString(response.body());
    }

}
