package dabusmc.minepacker.backend.authorisation.microsoft;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.authorisation.AbstractAccount;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.util.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.UUID;

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
    public static final String MICROSOFT_MINECRAFT_LOGIN_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    public static final String MICROSOFT_MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    public static final String MICROSOFT_MINECRAFT_ENTITLEMENTS_URL = "https://api.minecraftservices.com/entitlements/license";

    public XBLAuthResponse XstsToken;
    public LoginResponse AttemptedLoginResponse;

    public MicrosoftAccount() {
        super(AccountType.Microsoft);
    }

    public boolean refreshAccessToken() {
        return refreshAccessToken(false);
    }

    public boolean refreshAccessToken(boolean force) {
        try {
            if (force || new Date().after(this.AccessToken.ExpiresAt)) {
                Logger.message("MicrosoftAccount", "OAuth Access Token expired. Attempting to refresh");
                OAuthTokenResponse tokenResponse = refreshAccessToken(AccessToken.RefreshToken);

                if (tokenResponse == null) {
                    LoggedIn = false;
                    Logger.error("MicrosoftAccount", "Failed to refresh AccessToken");
                    return false;
                }

                this.AccessToken = tokenResponse;
            }

            if (force || new Date().after(this.XstsToken.NotAfter)) {
                Logger.message("MicrosoftAccount", "Xsts Auth expired. Attempting to get new Auth");
                XBLAuthResponse xboxLiveAuthResponse = getXBLToken(this.AccessToken.AccessToken);
                if (xboxLiveAuthResponse == null) {
                    Logger.error("MicrosoftAccount", "Failed to get XBLToken");
                    return false;
                }

                this.XstsToken = getXstsToken(xboxLiveAuthResponse.Token);

                if (XstsToken == null) {
                    LoggedIn = false;
                    Logger.error("MicrosoftAccount", "Failed to get XstsToken");
                    return false;
                }
            }

            if (force || new Date().after(this.AttemptedLoginResponse.ExpiresAt)) {
                LoginResponse loginResponse = loginToMinecraft("XBL3.0 x=" + XstsToken.DisplayClaims.XUI.get(0).UHS + ";" + XstsToken.Token);

                if (loginResponse == null) {
                    LoggedIn = false;
                    Logger.error("MicrosoftAccount", "Failed to login to Minecraft");
                    return false;
                }

                Entitlements entitlements = getEntitlements(loginResponse.AccessToken);

                if (!(entitlements.Items.stream().anyMatch(i -> i.Name.equalsIgnoreCase("product_minecraft"))
                        && entitlements.Items.stream().anyMatch(i -> i.Name.equalsIgnoreCase("game_minecraft")))) {
                    Logger.error("MicrosoftAccount", "This account doesn't have a valid purchase of Minecraft");
                    return false;
                }

                // make sure they have a Minecraft profile before saving logins
                if (!checkAndUpdateProfile(loginResponse.AccessToken)) {
                    LoggedIn = false;
                    return false;
                }

                this.AttemptedLoginResponse = loginResponse;
            }
        } catch (Exception e) {
            Logger.error("MicrosoftAccount", e);
            return false;
        }

        return true;
    }

    private boolean checkAndUpdateProfile(String accessToken) {
        Profile profile = getMCProfile(accessToken);

        if (profile == null) {
            Logger.error("MicrosoftAccount", "Failed to get Minecraft profile");
            return false;
        }

        this.AccountProfile = profile;

        return true;
    }

    public boolean ensureAccountIsLoggedIn() {
        if(!LoggedIn) {
            MinePackerRuntime.s_Instance.getAuthenticationManager().attemptMicrosoftLogin();
            return false;
        }

        return true;
    }

    public boolean ensureAccessTokenValid() {
        if (!ensureAccountIsLoggedIn()) {
            return false;
        }

        if (!new Date().after(this.AccessToken.ExpiresAt)) {
            return true;
        }

        Logger.message("MicrosoftAccount", "Access Token has expired. Attempting to refresh it.");

        try {
            return refreshAccessToken();
        } catch (Exception e) {
            Logger.error("MicrosoftAccount", e);
        }

        return false;
    }


    public static OAuthTokenResponse refreshAccessToken(String refreshToken) {
        String urlEncodedArgs = StringUtils.generateURLEncodedString(
                "client_id", MICROSOFT_LOGIN_CLIENT_ID,
                "refresh_token", refreshToken,
                "grant_type", "refresh_token",
                "redirect_uri", MICROSOFT_LOGIN_REDIRECT_URL
        );

        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().post(MICROSOFT_AUTH_TOKEN_URL,
                urlEncodedArgs, true, "Content-Type", "application/x-www-form-urlencoded");
        return OAuthTokenResponse.generateFromJSONString(response.body());
    }

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

    public static XBLAuthResponse getXstsToken(String xblToken) {
        JSONObject properties = new JSONObject();
        properties.put("SandboxId", "RETAIL");

        JSONArray userTokens = new JSONArray();
        userTokens.add(xblToken);
        properties.put("UserTokens", userTokens);

        JSONObject obj = new JSONObject();
        obj.put("Properties", properties);
        obj.put("RelyingParty", "rp://api.minecraftservices.com/");
        obj.put("TokenType", "JWT");

        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().post(MICROSOFT_XSTS_AUTH_TOKEN_URL,
                obj.toJSONString(), true, "Content-Type", "application/json", "Accept", "application/json", "x-xbl-contract-version",
                "1");
        return XBLAuthResponse.generateFromJSONString(response.body());
    }

    public static LoginResponse loginToMinecraft(String xstsToken) {
        JSONObject obj = new JSONObject();
        obj.put("identityToken", xstsToken);
        obj.put("ensureLegacyEnabled", true);

        // NOTE: While waiting for Mojang to allow this product to access this API, we can just use a temporary key that is related to a user
        // To fill this key out, follow this guide: https://kqzz.github.io/mc-bearer-token/. This needs to be done every 24 hours.
        String key = "\"\"";
        String tempResponse = "{\"username\": \"830491e6-50e9-e9ed-6e8a-7041f4fef585\", \"roles\": [], \"access_token\": " + key + ", \"token_type\": \"Bearer\", \"expires_in\": 86400}";

//        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().post(MICROSOFT_MINECRAFT_LOGIN_URL,
//                obj.toJSONString(), true, "Content-Type", "application/json", "Accept", "application/json");
        return LoginResponse.generateFromJSONString(tempResponse);
    }

    public static Entitlements getEntitlements(String accessToken) {
        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().get(
                String.format("%s?requestId=%s", MICROSOFT_MINECRAFT_ENTITLEMENTS_URL, UUID.randomUUID()),
                true,
                "Authorization", "Bearer " + accessToken, "Content-Type", "application/json", "Accept",
                "application/json");
        return Entitlements.generateFromJSONString(response.body());
    }

    public static Profile getMCProfile(String accessToken) {
        HttpResponse<String> response = MinePackerRuntime.s_Instance.getModApi().get(
                MICROSOFT_MINECRAFT_PROFILE_URL,
                true,
                "Authorization", "Bearer " + accessToken);
        return Profile.generateFromJSONString(response.body());
    }

    @Override
    public String getUserType() {
        return "msa";
    }
}
