package dk.dtu.compute.se.pisd.roborally.online;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;
import dk.dtu.compute.se.pisd.roborally.restful.RequestMaker;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Client extends RequestMaker{

    private ResourceLocation resourceLocation;
    private Game game;
    // private int clientId;

    /**
     *
     * @param requestMaker
     * @author ZeeDiazz (Zaid)
     */
    //public Client(RequestMaker requestMaker){this.requestMaker = requestMaker;}

    // If the player prefer a game with a specific gameID. If it's possible, it should make the game with given gameID. 
    // If not, a valid gameID should be used to create the game

    /**
     *
     * @param gameId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    Game createGame(int gameId) throws URISyntaxException, IOException, InterruptedException {
        //Create the request to the server to create the game
        URI createGameURI = makeUri(resourceLocation.baseLocation, resourceLocation.joinGame, String.valueOf(gameId));

        //Get request
        getRequest(createGameURI);

        //POST Request
        String stringTest = "";
        postRequest(createGameURI, stringTest);

        return game;
    }


    // If the player doesn't prefer to choose the gameID
    // Game will include the Player who makes the game

    /**
     *
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    Game createGame() throws URISyntaxException, IOException, InterruptedException {
        int num = 0;

        URI createGameURI = makeUri(resourceLocation.baseLocation, resourceLocation.joinGame, String.valueOf(num));

        getRequest(createGameURI);

        return game;
    }

    // Returns a game where there is the newly made player
    /*Game joinGame(int gameId);

    boolean canStartGame();

    void finishedProgrammingPhase();

    boolean canStartActivationPhase();

    void sendStatusInfo();

    // (Ask if the game is finished)
    boolean gameIsFinished();

    // (To get the clientId of the player, from the server)
    boolean getPlayerId();

    // (save game)
    void saveGame();

    // (load game)
    void loadGame();

    // (to perform the move of the other players, when activationPhase can begin)
    void simulateActivationPhase();*/
}
