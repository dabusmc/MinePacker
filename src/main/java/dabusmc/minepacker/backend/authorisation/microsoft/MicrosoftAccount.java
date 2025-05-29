package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.MinePackerApp;
import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.util.StringUtils;
import org.json.simple.JSONObject;

import java.net.http.HttpResponse;

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
    public static final String MICROSOFT_AUTH_TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";



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

}
