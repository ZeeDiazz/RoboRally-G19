package dk.dtu.compute.se.pisd.roborally.restful;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * This class is used to make the responses for the RESTful API.
 * @param <T> generic type
 * @auther Daniel Jensen
 */
@Component

public class ResponseMaker<T> {
    /**
     * This method is used to make the response for the RESTful API.
     * @param item
     * @return a response with the HTTP status code
     * @auther Daniel Jensen
     */
    public ResponseEntity<T> itemResponse(T item) {
        // If there is no item, make the response "Not Found" (404), otherwise the response is "OK" (200)
        HttpStatusCode statusCode = (item == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        return new ResponseEntity<>(item, statusCode);
    }

    public ResponseEntity<T> ok() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<T> notFound() { return new ResponseEntity<>(HttpStatus.NOT_FOUND); }

    public ResponseEntity<T> created(T item) {
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    public ResponseEntity<T> accepted() {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<T> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<T> notModified() {
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    public ResponseEntity<T> unauthorized() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<T> forbidden() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<T> methodNotAllowed() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    public ResponseEntity<T> tooManyRequests() {
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    public ResponseEntity<T> serverError() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<T> notImplemented() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
