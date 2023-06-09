package dk.dtu.compute.se.pisd.roborally.online;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;

import dk.dtu.compute.se.pisd.roborally.restful.RequestMaker;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import dk.dtu.compute.se.pisd.roborally.restful.Response;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private Game game;
    private final String baseLocation;
    private Thread listener;


    private int playerId;
    private Map<String, String> lobbyAndPlayerInfo;

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
     * @param gameId                          Game ID that the player prefers
     * @param minimumsNumbersOfPlayersToStart Minimum amount of players to start the game
     * @param boardName                       The name of the board
     * @return Returns a game that has this player in it, and a Game ID
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    // . 
    public Game createGame(int gameId, int minimumsNumbersOfPlayersToStart, String boardName) throws URISyntaxException, IOException, InterruptedException {
        //Create the request to the server to create the game

        if (gameId < 0) {
            this.createGame(minimumsNumbersOfPlayersToStart,boardName);
        }
        
        int minimumPlayers = (minimumsNumbersOfPlayersToStart >= 2 && minimumsNumbersOfPlayersToStart <= 6) ? minimumsNumbersOfPlayersToStart : 2;

        URI gameURI = new URI(makeFullUri(ResourceLocation.specificGame));

        

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);
        jsonObject.addProperty("minimumsNumbersOfPlayers", minimumsNumbersOfPlayersToStart);
        jsonObject.addProperty("boardName", boardName);

        Response<JsonObject> jsonGameFromServer = RequestMaker.postRequestJson(gameURI, jsonObject);


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
     * @param boardName                     The name of the board
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
    public int joinGame(int gameId) throws IOException, InterruptedException, URISyntaxException {
        URI joinGameURI = new URI(makeFullUri(ResourceLocation.joinGame));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);

        Response<JsonObject> joinInfo = RequestMaker.postRequestJson(joinGameURI, jsonObject);

        if (joinInfo.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = joinInfo.getItem();

         /*   Game joinedGame = new OnlineGame(new Board(10, 10), gameFromServer.getAsJsonObject("game").get("numberOfPlayersToStart").getAsInt());
            joinedGame.deserialize(gameFromServer);*/

            playerId = gameFromServer.get("playerId").getAsInt(); //??
            System.out.println("Joined gameId: " + gameId);

            listener = new Thread(() -> {
                lobbyAndPlayerInfo = new HashMap<>();
                lobbyAndPlayerInfo.put("lobbyId", gameId + "");
                lobbyAndPlayerInfo.put("playerId", playerId + "");
                URI statusUri;
                try {
                    statusUri = RequestMaker.makeUri(makeFullUri(ResourceLocation.gameStatus), lobbyAndPlayerInfo);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                boolean hasStarted = false;
                while (!hasStarted) {
                    try {
                        hasStarted = RequestMaker.getRequestJson(statusUri).getItem().get("hasStarted").getAsBoolean();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // wait
                }

                JsonObject gameInfo;
                try {
                    URI gameUri = RequestMaker.makeUri(this.makeFullUri(ResourceLocation.specificGame), lobbyAndPlayerInfo);
                    gameInfo = RequestMaker.getRequestJson(gameUri).getItem();
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                Board board = new Board(0, 0);
                board = (Board)board.deserialize(gameInfo.get("board"));
                int playerCount = gameInfo.get("playerCount").getAsInt();

                game = new OnlineGame(board, playerCount);
                JsonArray playerArray = gameInfo.get("players").getAsJsonArray();
                Player playerDeserializer = new OnlinePlayer(null, "", "");
                for (int i = 0; i < playerCount; i++) {
                    game.addPlayer((Player)playerDeserializer.deserialize(playerArray.get(i)));
                }
            });
            listener.start();
            return playerId;
        } else {
            System.out.println("Failed to join gameId: " + gameId);
            return -1;
        }
    }

    /**
     * Returns whether the game can be started
     *
     * @param gameId The game ID of the game, that needs to be checked
     * @return returns true if the game can be started, and false if it can't
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */

    public boolean canStartGame(int gameId) throws URISyntaxException, IOException, InterruptedException {

        URI canStartGameURI = RequestMaker.makeUri(ResourceLocation.baseLocation, ResourceLocation.gameStatus, String.valueOf(gameId));


        Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(canStartGameURI);

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

    void finishedProgrammingPhase() {
        
    }
/*

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


    private String makeFullUri(String relativeDestination) {
        return baseLocation + relativeDestination;
    }

    public boolean gameIsReady() {
        return this.game != null;
    }

    public Game getGame() {
        return this.game;
    }
}
