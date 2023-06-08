package dk.dtu.compute.se.pisd.roborally.server;

import dk.dtu.compute.se.pisd.roborally.restful.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.getRequest;
import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.makeUri;

public class GreetingClient {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        URI requestUri = makeUri("http://localhost:8190/greeting","name","John");
        Response<String> name = getRequest(requestUri);
        if(name.hasItem()){
            System.out.println(name.getStatusCode());
        }

    }
}
