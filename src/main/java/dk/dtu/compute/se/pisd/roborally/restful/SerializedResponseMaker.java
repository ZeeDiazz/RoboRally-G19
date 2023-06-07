package dk.dtu.compute.se.pisd.roborally.restful;

import com.google.gson.JsonElement;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class SerializedResponseMaker<T extends Serializable> extends ResponseMaker<JsonElement> {
    public ResponseEntity<JsonElement> itemResponse(T item) {
        // If there is no item, make the response "Not Found" (404), otherwise the response is "OK" (200)
        HttpStatusCode statusCode = (item == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(item.serialize(), statusCode);
    }

    public ResponseEntity<JsonElement> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<JsonElement> created(T item) {
        return new ResponseEntity<>(item.serialize(), HttpStatus.CREATED);
    }

    public ResponseEntity<JsonElement> accepted() {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<JsonElement> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<JsonElement> notModified() {
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    public ResponseEntity<JsonElement> unauthorized() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<JsonElement> forbidden() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<JsonElement> methodNotAllowed() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    public ResponseEntity<JsonElement> tooManyRequests() {
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    public ResponseEntity<JsonElement> serverError() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<JsonElement> notImplemented() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
