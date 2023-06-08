package dk.dtu.compute.se.pisd.roborally.online;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;

import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import dk.dtu.compute.se.pisd.roborally.restful.Response;
import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.getRequest;
import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.makeUri;
import static dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation.baseLocation;
import static dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation.joinGame;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Client{


    private static Game game;
    // private int clientId;


    // If the player prefer a game with a specific gameID. If it's possible, it should make the game with given gameID. 
    // If not, a valid gameID should be used to create the game
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        createGame(0);
    }
    /**
     *
     * @param gameId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    public static Game createGame(int gameId) throws URISyntaxException, IOException, InterruptedException {
        /*
        * Post = lobby, gamId
        * getter = board
        * post = conformation
        * return Game;
        * */

        //Create the request to the server to create the game
        URI createGameURI = makeUri(baseLocation, joinGame, String.valueOf(gameId));

        //Get request
        Response<String> getUriRequest = getRequest(createGameURI);

        if(getUriRequest .hasItem()){
            System.out.println(getUriRequest.getStatusCode());
        }
        //POST Request
        //postRequest(createGameURI, );

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

        URI createGameURI = makeUri(baseLocation, joinGame, String.valueOf(num));

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
