package dk.dtu.compute.se.pisd.roborally.server;

import org.springframework.http.ResponseEntity;

public record Response(ResponseEntity responseEntity, String info) {
}
