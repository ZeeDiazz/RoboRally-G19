package dk.dtu.compute.se.pisd.roborally.online;

import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;

import java.io.IOException;
import java.net.URISyntaxException;

public class ClientController {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Client client = new Client(ResourceLocation.baseLocation);
        client.createGame(123, 3, "");
        int playerId = client.joinGame(123);
        System.out.println("Given id: " + playerId);
    }
}
