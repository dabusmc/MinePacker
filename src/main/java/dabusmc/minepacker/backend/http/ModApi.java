package dabusmc.minepacker.backend.http;

import dabusmc.minepacker.backend.analytics.Analytics;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.http.apis.ModrinthApi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public abstract class ModApi {

    public static ModApi Create(ModApiType type) {
        switch(type) {
            case Modrinth -> {
                return new ModrinthApi();
            }
            default -> {
                Logger.fatal("ModApi", "ModApiType isn't currently implemented");
            }
        }

        return null;
    }

    private HttpClient m_Client = null;
    private JSONParser m_Parser = null;
    private boolean m_Connected = false;

    protected ModApi() {
        // TODO: Look at this default client and check if it meets what we need it to do
        m_Client = HttpClient.newHttpClient();
        m_Parser = new JSONParser();

        try {
            URL testURL = new URI("https://google.com/").toURL();

            URLConnection connection = testURL.openConnection();
            connection.connect();

            Logger.info("ModApi", "Internet connection established");
            m_Connected = true;
        } catch (Exception e) {
            Logger.fatal("ModApi", "No Internet Connection available, please connect with internet");
            m_Connected = false;
        }
    }

    public boolean isConnected() {
        return m_Connected;
    }

    protected String getFinalURL(String baseUrl, String extension) {
        return baseUrl + "/" + extension;
    }

    public HttpResponse<String> get(String url, boolean statusCodeErrorCheck)
    {
        return get(url, statusCodeErrorCheck, "Content-Type", "application/json");
    }

    public HttpResponse<String> get(String url, boolean statusCodeErrorCheck, String... headers)
    {
        Analytics.begin("GETRequest");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(1)) // TODO: Check if this is a suitable duration to wait (might need lowering)
                .headers(headers) // FIXME: User-Agent header needs to be changed to be compliant with Modrinth standards
                .GET()
                .build();

        try {
            HttpResponse<String> response = m_Client.send(request, HttpResponse.BodyHandlers.ofString());

            if (statusCodeErrorCheck && response.statusCode() != 200) {
                Logger.fatal("ModApi", "Request threw status code " + response.statusCode() + "\n" + response.body());
                return null;
            }

            m_Connected = true;
            return response;
        } catch (IOException | InterruptedException e) {
            m_Connected = false;
            Logger.fatal("ModApi", e.toString());
        }

        Analytics.end("GETRequest");

        return null;
    }

    public HttpResponse<String> post(String url, JSONObject data, boolean statusCodeErrorCheck) {
        return post(url, data, statusCodeErrorCheck, "Content-Type", "application/json");
    }

    public HttpResponse<String> post(String url, Object data, boolean statusCodeErrorCheck, String... headers) {
        Analytics.begin("POSTRequest");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(1)) // TODO: Check if this is a suitable duration to wait (might need lowering)
                .headers(headers)
                .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .build();

        try {
            HttpResponse<String> response = m_Client.send(request, HttpResponse.BodyHandlers.ofString());

            if (statusCodeErrorCheck && response.statusCode() != 200) {
                Logger.fatal("ModApi", "Request threw status code " + response.statusCode() + "\n" + response.body());
                return null;
            }

            m_Connected = true;
            return response;
        } catch (IOException | InterruptedException e) {
            m_Connected = false;
            Logger.fatal("ModApi", e.toString());
        }

        Analytics.end("POSTRequest");

        return null;
    }

    protected JSONObject convertHttpResponseToJSONObject(HttpResponse<String> response) {
        try {
            if (response != null) {
                String res = response.body();
                Object obj = m_Parser.parse(res);
                return (JSONObject) obj;
            } else {
                Logger.fatal("ModApi", "Http Get returned null response");
            }
        } catch (ParseException e) {
            Logger.fatal("ModApi", e.toString());
        }

        return null;
    }

    protected JSONArray convertHttpResponseToJSONArray(HttpResponse<String> response) {
        try {
            if (response != null) {
                String res = response.body();
                Object obj = m_Parser.parse(res);
                return (JSONArray) obj;
            } else {
                Logger.fatal("ModApi", "Http Get returned null response");
            }
        } catch (ParseException e) {
            Logger.fatal("ModApi", e.toString());
        }

        return null;
    }

    public long downloadFromURL(String url, String fileName) {
        Analytics.begin("DownloadFile");

        try(InputStream in = URI.create(url).toURL().openStream()) {
            //Logger.message("ModApi", "Downloaded file at '" + url + "'");
            Analytics.end("DownloadFile");
            return Files.copy(in, Paths.get(fileName));
        } catch (IOException e) {
            Logger.error("ModApi", e.toString());
        }

        Analytics.end("DownloadFile");
        return 0;
    }

    public abstract boolean modIDExists(String id);
    public abstract Mod getModFromID(String id);

    public abstract JSONObject search(String query, int pageIndex, int pageLimit);

    public abstract String generateFacets();

    public abstract Mod constructModFromJsonObject(JSONObject obj);
}
