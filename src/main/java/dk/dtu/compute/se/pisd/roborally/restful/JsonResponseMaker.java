package dk.dtu.compute.se.pisd.roborally.restful;

import com.google.gson.JsonElement;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * This class is used to make a response with a JsonElement and an HTTP status code
 * @param <T> a generic type that extends JsonElement
 * @auther Felix Schmidt
 * @auther Daniel Jensen
 */
public class JsonResponseMaker<T extends JsonElement> extends ResponseMaker<String>  {
    public ResponseEntity<String> itemResponse(T item) {
        // If there is no item, make the response "Not Found" (404), otherwise the response is "OK" (200)
        HttpStatusCode statusCode = (item == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(item.toString(), statusCode);
    }

    /**
     * This method is used to make a response with the HTTP status code "OK" (200)
     * @return a response with the HTTP status code "OK" (200)
     * @auther Felix Schmidt
     */
    public ResponseEntity<String> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Override
    public ResponseEntity<String> notFound() { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }
    @Override
    public ResponseEntity<String> accepted() {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @Override
    public ResponseEntity<String> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Override
    public ResponseEntity<String> notModified() {
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
    @Override
    public ResponseEntity<String> unauthorized() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    @Override
    public ResponseEntity<String> forbidden() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    @Override
    public ResponseEntity<String> methodNotAllowed() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
    @Override
    public ResponseEntity<String> tooManyRequests() {
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }
    @Override
    public ResponseEntity<String> serverError() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @Override
    public ResponseEntity<String> notImplemented() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
