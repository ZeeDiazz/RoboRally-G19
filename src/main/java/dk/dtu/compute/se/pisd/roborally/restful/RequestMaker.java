package dk.dtu.compute.se.pisd.roborally.restful;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class RequestMaker {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final JsonParser jsonParser = new JsonParser();

    private static String encodeKeyAndValue(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8)
                + '='
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static URI makeUri(String base, String key, String value) throws URISyntaxException {
        return new URI(base + '?' + encodeKeyAndValue(key, value));
    }

    public static URI makeUri(String base, Map<String, String> parameters) throws URISyntaxException {
        Set<String> keys = parameters.keySet();
        String[] encoded = new String[keys.size()];
        int encodedIndex = 0;
        for (String key : keys) {
            encoded[encodedIndex++] = encodeKeyAndValue(key, parameters.get(key));
        }

        String keysAndValues = String.join("&", encoded);
        return new URI(base + '?' + keysAndValues);
    }

    public static Response<String> getRequest(URI location) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(location).GET().build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new Response<>(httpResponse);
    }

    public static Response<JsonObject> getRequestJson(URI location) throws IOException, InterruptedException {
        return new JsonResponse(getRequest(location));
    }

    public static Response<String> postRequest(URI location, String string) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(location)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(string))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new Response<>(httpResponse);
    }

    public static Response<JsonObject> postRequestJson(URI location, String string) throws IOException, InterruptedException {
        return new JsonResponse(postRequest(location, string));
    }
}
