package com.spiritlight.adapters.fabric.misc.connection;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Objects;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;

public class HttpRequests {
    static final OkHttpClient client = new OkHttpClient();
    static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=utf-8");

    public static String get(String url) {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (NullPointerException | IOException ex) {
            LogManager.getLogger("FishHelper/Connection")
                    .error("Failed to retrieve data from {}: ", url, ex);
            return ex.toString();
        }
    }

    public static String post(String url, String content) {
        Request request =
                new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(content, APPLICATION_JSON))
                        .header("User-Agent", "SpiritLight/1.21")
                        .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (NullPointerException | IOException ex) {
            LogManager.getLogger("FishHelper/Connection")
                    .error("Failed to post data to " + url + ": ", ex);
            return ex.toString();
        }
    }

    public static String postToWebhook(String url, String message) {
        JsonObject data = new JsonObject();
        data.addProperty("content", message);
        return post(url, data.toString());
    }
}
