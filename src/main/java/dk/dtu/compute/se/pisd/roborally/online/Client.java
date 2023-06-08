package dk.dtu.compute.se.pisd.roborally.online;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Board;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.OnlineGame;
import dk.dtu.compute.se.pisd.roborally.restful.Response;

import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.*;
import static dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation.*;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private Game game;
    private String baseLocation;


    // private int clientId;

    //TODO: CREATE a constructor with URI??
    public Client(String baseLocation) {
        this.baseLocation = baseLocation;
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
    public Game createGame(Integer gameId, int minimumsNumbersOfPlayers, String boardName) throws URISyntaxException, IOException, InterruptedException {
        //Create the request to the server to create the game

        int minimumPlayers = (minimumsNumbersOfPlayers >= 2 && minimumsNumbersOfPlayers <= 6) ? minimumsNumbersOfPlayers : 2;

        URI gameURI = makeURI(specificGame);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);
        jsonObject.addProperty("minimumsNumbersOfPlayers", minimumsNumbersOfPlayers);
        jsonObject.addProperty("boardName", boardName);

        Response<JsonObject> jsonGameFromServer = postRequestJson(gameURI, jsonObject);


        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            Game initialGame = new OnlineGame(new Board(10, 10), minimumPlayers);
            game = (OnlineGame) initialGame.deserialize(gameFromServer);


            System.out.println("gameId: " + game.getGameId());
            System.out.println("minimum number of players to start: " + minimumPlayers);

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
    Game createGame(int minimumNumberOfPlayersToStart, String boardName) throws URISyntaxException, IOException, InterruptedException {
        return createGame(null, minimumNumberOfPlayersToStart, boardName);
    }

    // TODO - Update JoinGame with new logic

    // Returns a game where there is the newly made player
    Game joinGame(int gameId) throws IOException, InterruptedException, URISyntaxException {

        URI joinGameURI = makeURI(joinGame);
        Response<JsonObject> jsonGameFromServer = postRequestJson(joinGameURI,gameId+"");

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            Game joinedGame = new OnlineGame(new Board(10, 10), gameFromServer.getAsJsonObject("game").get("numberOfPlayersToStart").getAsInt());
            joinedGame.deserialize(gameFromServer);

            System.out.println("Joined gameId: " + joinedGame.getGameId());

            return joinedGame;
        } else {
            System.out.println("Failed to join gameId: " + gameId);
            return null;
        }
    }

    /*boolean canStartGame();

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


    private URI makeURI(String destination) throws URISyntaxException {
        return new URI(baseLocation + destination);
    }


}
