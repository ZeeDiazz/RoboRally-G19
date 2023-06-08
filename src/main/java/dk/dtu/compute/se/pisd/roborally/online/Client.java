package dk.dtu.compute.se.pisd.roborally.online;

import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Board;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.OnlineGame;
import dk.dtu.compute.se.pisd.roborally.restful.Response;

import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.*;
import static dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation.baseLocation;
import static dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation.joinGame;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private Game game;
    // private int clientId;

    //TODO: CREATE a constructor with URI??
    public Client() {

    }

    // If the player prefer a game with a specific gameID. If it's possible, it should make the game with given gameID. 
    // If not, a valid gameID should be used to create the game

    /**
     * @param gameId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    // If the player prefer a game with a specific gameID. If it's possible, it should make the game with given gameID. 

    public Game createGame(Integer gameId, int numberOfPlayersToStart) throws URISyntaxException, IOException, InterruptedException {
        //Create the request to the server to create the game

        int minimumNumberOfPlayersToStart = (numberOfPlayersToStart >= 2 && numberOfPlayersToStart <= 6) ? numberOfPlayersToStart : 2;

        Map<String, String> values = new HashMap<>(1);
        values.put(String.valueOf(gameId), String.valueOf(numberOfPlayersToStart));

        URI createGameURI = makeUri(baseLocation + joinGame, values);

        // Talk with Felixo 
        //create a gameId on the server
        Response<JsonObject> jsonGameFromServer = postRequestJson(createGameURI, String.valueOf(gameId));

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            Game initialGame = new OnlineGame(new Board(10, 10), minimumNumberOfPlayersToStart);
            game = (OnlineGame) initialGame.deserialize(gameFromServer);


            System.out.println("gameId: " + game.getGameId());
            System.out.println("minimum number of players to start: " + minimumNumberOfPlayersToStart);

            return game;
        } else {
            System.out.println("Failed gameId: " + gameId);
            return null;
        }
    }
    

    /**
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */

    // If the player doesn't prefer to choose the gameID
    // Game will include the Player who makes the game
    Game createGame(int minimumNumberOfPlayersToStart) throws URISyntaxException, IOException, InterruptedException {
        return createGame(null, minimumNumberOfPlayersToStart);
    }

    // TODO - Where does the client select the board

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
