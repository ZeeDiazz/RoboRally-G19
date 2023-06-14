package dk.dtu.compute.se.pisd.roborally.restful;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class is used to parse the response from the server into a JsonObject.
 * @auther Daniel Jensen
 */
public class JsonResponse extends Response<JsonObject> {
    private static final JsonParser parser = new JsonParser();

    /**
     * Constructor for the JsonResponse class.
     * @param stringResponse
     * @auther Daniel Jensen
     */
    public JsonResponse(Response<String> stringResponse) {
        super(stringResponse.getStatusCode(), parser.parse(stringResponse.item).getAsJsonObject());
    }
}
