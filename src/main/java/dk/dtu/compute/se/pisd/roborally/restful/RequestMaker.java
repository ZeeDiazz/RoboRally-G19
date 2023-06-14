package dk.dtu.compute.se.pisd.roborally.restful;

import com.google.gson.JsonElement;
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

/**
 * This class is used to make requests to the server.
 * @auther Daniel Jensen
 */

public abstract class RequestMaker {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final JsonParser jsonParser = new JsonParser();

    private static String encodeKeyAndValue(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8)
                + '='
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * This method is used to make a URI with a base, key and value.
     * @param base
     * @param key
     * @param value
     * @return a URI with a base, key and value
     * @throws URISyntaxException
     * @auther Daniel Jensen
     */
    public static URI makeUri(String base, String key, String value) throws URISyntaxException {
        return new URI(base + '?' + encodeKeyAndValue(key, value));
    }

    /**
     * This method is used to make a URI with a base and a map of parameters.
     * @param base
     * @param parameters
     * @return a URI with a base and a map of parameters
     * @throws URISyntaxException
     * @auther Daniel Jensen
     */
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

    /**
     * This method is used to generate a get request and return the response.
     * @param location
     * @return http response
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */
    public static Response<String> getRequest(URI location) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(location).GET().build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new Response<>(httpResponse);
    }

    /**
     * This method is used to generate a get request and return the response as a JsonObject.
     * @param location
     * @return JsonResponse
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */
    public static Response<JsonObject> getRequestJson(URI location) throws IOException, InterruptedException {
        return new JsonResponse(getRequest(location));
    }

    /**
     * This method is used to generate a post request and return the response.
     * @param location
     * @param string
     * @return http response
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */

    public static Response<String> postRequest(URI location, String string) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(location)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(string))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new Response<>(httpResponse);
    }
    /**
     * This method is used to generate a post request and return the response as a JsonObject.
     * @param location
     * @param json
     * @return JsonResponse
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */
    public static Response<String> postRequest(URI location, JsonElement json) throws IOException, InterruptedException {
        return postRequest(location, json.toString());
    }

    /**
     * This method is used to generate a post request and return the response as a JsonObject.
     * @param location
     * @param string
     * @return JsonResponse
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */
    public static Response<JsonObject> postRequestJson(URI location, String string) throws IOException, InterruptedException {
        Response<String> response = postRequest(location, string);
        try {
            return new JsonResponse(response);
        }
        catch (IllegalStateException e) {
            System.out.println("Posted to: '" + location + "' with payload: '" + string + "'");
            System.out.println("Response: " + response.getItem());
            throw e;
        }
    }

    /**
     * This method is used to generate a post request and return the response as a JsonObject.
     * @param location
     * @param json
     * @return JsonResponse
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */
    public static Response<JsonObject> postRequestJson(URI location, JsonElement json) throws IOException, InterruptedException {
        return postRequestJson(location, json.toString());
    }

    public static Response<JsonObject> postRequestJson(URI location, Map<String, String> map) throws IOException, InterruptedException {
        JsonObject json = new JsonObject();
        for (String key : map.keySet()) {
            json.addProperty(key, map.get(key));
        }
        return postRequestJson(location, json);
    }

    /**
     * This method is used to generate a delete request. It does not return anything.
     * @param location
     * @throws IOException
     * @throws InterruptedException
     * @auther Daniel Jensen
     */
    public static Response<String> deleteRequest(URI location) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(location)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        return new Response<>(client.send(request, HttpResponse.BodyHandlers.ofString()));
    }
}
