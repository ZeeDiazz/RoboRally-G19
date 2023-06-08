package dk.dtu.compute.se.pisd.roborally.server;

import org.springframework.http.ResponseEntity;

public record Message(ResponseEntity responseEntity, String info) {
}
