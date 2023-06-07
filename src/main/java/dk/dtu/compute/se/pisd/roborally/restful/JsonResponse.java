package dk.dtu.compute.se.pisd.roborally.restful;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonResponse extends Response<JsonObject> {
    private static final JsonParser parser = new JsonParser();

    public JsonResponse(Response<String> stringResponse) {
        super(stringResponse.getStatusCode(), parser.parse(stringResponse.item).getAsJsonObject());
    }
}
