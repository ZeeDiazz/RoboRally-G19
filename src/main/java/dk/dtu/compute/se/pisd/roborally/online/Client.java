package dk.dtu.compute.se.pisd.roborally.online;

import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Board;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.OnlineGame;
import dk.dtu.compute.se.pisd.roborally.restful.RequestMaker;
import dk.dtu.compute.se.pisd.roborally.restful.Response;

import static dk.dtu.compute.se.pisd.roborally.restful.RequestMaker.*;
import static dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation.*;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLOutput;

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
     * Creates a game, where the player prefers a game with a specific Game ID.
     * If it's possible, it should make the game with given Game ID
     * If not, a random gameId will be returned with the game from Server
     *
     * @param gameId Game ID that the player prefers
     * @param minimumsNumbersOfPlayersToStart Minimum amount of players to start the game
     * @param boardName The name of the board
     * @return Returns a game that has this player in it, and a Game ID
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    // . 
    public Game createGame(int gameId, int minimumsNumbersOfPlayersToStart, String boardName) throws URISyntaxException, IOException, InterruptedException {
        //Create the request to the server to create the game

        int minimumPlayers = (minimumsNumbersOfPlayersToStart >= 2 && minimumsNumbersOfPlayersToStart <= 6) ? minimumsNumbersOfPlayersToStart : 2;

        URI gameURI = makeURI(specificGame);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);
        jsonObject.addProperty("minimumsNumbersOfPlayers", minimumsNumbersOfPlayersToStart);
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
     * Creates a game with a random gameId
     *
     * @param minimumNumberOfPlayersToStart Minimum amount of players to start the game
     * @param boardName The name of the board
     * @return Returns a game that has this player in it, and a Game ID
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */

    // If the player doesn't prefer to choose the gameID
    // Game will include the Player who makes the game
    public Game createGame(int minimumNumberOfPlayersToStart, String boardName) throws URISyntaxException, IOException, InterruptedException {
        return createGame(-1, minimumNumberOfPlayersToStart, boardName);
    }

    // TODO - Update JoinGame with new logic

    // Returns a game where there is the newly made player

    // Unsure what type it should return
    public void joinGame(int gameId) throws IOException, InterruptedException, URISyntaxException {

        URI joinGameURI = makeURI(joinGame);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);

        Response<JsonObject> jsonGameFromServer = postRequestJson(joinGameURI, jsonObject);

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

         /*   Game joinedGame = new OnlineGame(new Board(10, 10), gameFromServer.getAsJsonObject("game").get("numberOfPlayersToStart").getAsInt());
            joinedGame.deserialize(gameFromServer);*/

            gameId = gameFromServer.get("gameId").getAsInt();

            System.out.println("Joined gameId: " + gameId);

        } else {
            System.out.println("Failed to join gameId: " + gameId);
        }
    }

    /**
     * Returns whether the game can be started
     * @param gameId The game ID of the game, that needs to be checked
     * @return returns true if the game can be started, and false if it can't
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */

    public boolean canStartGame(int gameId) throws URISyntaxException, IOException, InterruptedException {

        URI canStartGameURI = makeUri(baseLocation, gameStatus, String.valueOf(gameId));


        Response<JsonObject> jsonGameFromServer = getRequestJson(canStartGameURI);

        boolean canStart;
        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            canStart = gameFromServer.get("canStartGame").getAsBoolean();

            if (canStart) {
                System.out.println("The game can be started");
            } else {
                System.out.println("The game can't be started yet");
            }
        } else {
            System.out.println("Failed to connect");
            return false;
        }

        return canStart;

    }
/*
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
