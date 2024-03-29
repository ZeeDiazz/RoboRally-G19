package dk.dtu.compute.se.pisd.roborally.online;

import com.google.gson.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;

import dk.dtu.compute.se.pisd.roborally.restful.RequestMaker;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import dk.dtu.compute.se.pisd.roborally.restful.Response;
import javafx.scene.control.Alert;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Client {
    private int gameId;
    private int playerIndex = -1;
    private Game game;
    private final String baseLocation;
    private Thread listener;
    private int playerId;
    private JsonArray jsonMovesArray;
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    public Client(String baseLocation) {
        super();
        this.baseLocation = baseLocation;
    }

    // If the player prefer a game with a specific gameID. If it's possible, it should make the game with given gameID. 
    // If not, a valid gameID should be used to create the game

    /**
     * Creates a game, where the player prefers a game with a specific Game ID.
     * If it's possible, it should make the game with given Game ID
     * If not, a random gameId will be returned with the game from Server
     *
     * @param gameId                Game ID that the player prefers
     * @param minimumPlayersToStart Minimum amount of players to start the game
     * @param boardName             The name of the board
     * @return Returns a game that has this player in it, and a Game ID
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author Zigalow & ZeeDiazz (Zaid)
     */
    // . 
    public void createGame(int gameId, int minimumPlayersToStart, String boardName) throws URISyntaxException, IOException, InterruptedException {
        //Create the request to the server to create the game

        if (gameId < 0) {
            Random rng = new Random();
            gameId = rng.nextInt(0, Integer.MAX_VALUE);
        }

        int minimumPlayers = (minimumPlayersToStart >= 2 && minimumPlayersToStart <= 6) ? minimumPlayersToStart : 2;

        URI gameURI = new URI(makeFullUri(ResourceLocation.specificGame));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);
        jsonObject.addProperty("minimumPlayers", minimumPlayersToStart);
        jsonObject.addProperty("boardName", boardName);

        Response<JsonObject> jsonGameFromServer = RequestMaker.postRequestJson(gameURI, jsonObject);

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            gameId = jsonGameFromServer.getItem().get("gameId").getAsInt();
            // needs playerID
            int playerId = jsonGameFromServer.getItem().get("playerId").getAsInt();
            System.out.println("gameId: " + gameId);
            System.out.println("minimum number of players to start: " + minimumPlayers);

            this.playerId = playerId;
            this.gameId = gameId;
            this.playerIndex = 0;
        } else {
            System.out.println("Failed gameId: " + gameId);
            System.out.println(jsonGameFromServer.getStatusCode());
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
    public void createGame(int minimumNumberOfPlayersToStart, String boardName) throws URISyntaxException, IOException, InterruptedException {
        createGame(-1, minimumNumberOfPlayersToStart, boardName);
    }


    public int joinGame(int gameId) throws IOException, InterruptedException, URISyntaxException {
        URI joinGameURI = new URI(makeFullUri(ResourceLocation.joinGame));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);

        Response<JsonObject> joinInfo = RequestMaker.postRequestJson(joinGameURI, jsonObject);

        if (joinInfo.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = joinInfo.getItem();


            this.playerId = gameFromServer.get("playerId").getAsInt(); //??
            this.playerIndex = gameFromServer.get("playerIndex").getAsInt();
            this.gameId = gameId;
            System.out.println("Joined game: " + gameId + " as player: " + playerId);

            listener = new Thread(() -> {
                URI statusUri;
                try {
                    statusUri = RequestMaker.makeUri(makeFullUri(ResourceLocation.gameStatus), getIdentification());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Status uri: " + statusUri);

                Response<JsonObject> currentStatus;
                boolean hasStarted = false;
                while (!hasStarted) {
                    try {
                        currentStatus = RequestMaker.getRequestJson(statusUri);
                        hasStarted = currentStatus.getItem().get("hasStarted").getAsBoolean();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                JsonObject gameInfo;
                try {
                    URI gameUri = RequestMaker.makeUri(this.makeFullUri(ResourceLocation.specificGame), getIdentification());
                    gameInfo = RequestMaker.getRequestJson(gameUri).getItem();
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                game = deserializeGameFromServer(gameInfo);
            });
            listener.start();
            if (playerId > 0) {
                System.out.println("Succesfully joined game");
            } else {
                // playerId == 0 means the game is full : playerID == -1 means that the game doesn't exist
                System.out.println(playerId == 0 ? "Game is full" : "Game doesn't exist");
            }
            return playerId;
        } else {
            System.out.println("Failed to join gameId: " + gameId);
            return -1;
        }
    }

    /**
     * Returns whether the game can be started
     *
     * @return returns true if the game can be started, and false if it can't
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */

    public boolean canStartGame() throws URISyntaxException, IOException, InterruptedException {

        URI canStartGameURI = RequestMaker.makeUri(makeFullUri(ResourceLocation.gameStatus), "gameId", String.valueOf(gameId));

        Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(canStartGameURI);

        boolean canStart;
        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            canStart = gameFromServer.get("canLaunch").getAsBoolean();

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

    public void finishedProgrammingPhase(Command[] commands) throws URISyntaxException, IOException, InterruptedException {
        URI finishedProgrammingPhaseURI = new URI(makeFullUri(ResourceLocation.gameStatus));

        JsonObject request = new JsonObject();
        request.addProperty("gameId", gameId);
        request.addProperty("playerId", playerId);
        JsonArray cards = new JsonArray();
        for (Command command : commands) {
            if (command == null) {
                cards.add("");
            } else {
                cards.add(command.toString());
            }
        }
        request.add("moves", cards);
        request.addProperty("isReady", true);

        Response<String> jsonProgrammingPhase = RequestMaker.postRequest(finishedProgrammingPhaseURI, request);

        if (jsonProgrammingPhase.getStatusCode().is2xxSuccessful()) {
            System.out.println("Successfully sent the ProgrammingPhase");
        } else {
            System.out.println("Failed to connect");
        }
    }


    public boolean canStartActivationPhase() throws URISyntaxException, IOException, InterruptedException {
        URI canStartActivationPhaseURI = RequestMaker.makeUri(makeFullUri(ResourceLocation.gameStatus), getIdentification());

        Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(canStartActivationPhaseURI);

        boolean canStartActivationPhase;
        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            canStartActivationPhase = gameFromServer.get("isReady").getAsBoolean();
            if (gameFromServer.has("moves")) {
                jsonMovesArray = gameFromServer.get("moves").getAsJsonArray();
                System.out.println("Moves gotten: " + jsonMovesArray);
            } else {
                jsonMovesArray = null;
            }
        } else {
            System.out.println("Failed to connect");
            return false;
        }

        return canStartActivationPhase;
    }

    // Todo - ask Daniel: What's the idea of the method
    public void sendStatusInfo() throws URISyntaxException, IOException, InterruptedException {
        URI gameStatueURI = new URI(makeFullUri(ResourceLocation.gameStatus));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameId", gameId);
        jsonObject.addProperty("playerId", playerId);


        Response<JsonObject> jsonGameInfoFromServer = RequestMaker.postRequestJson(gameStatueURI, jsonObject);
        if (jsonGameInfoFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameInfoFromServer.getItem();

            int numberOfPlayers = gameFromServer.get("playerCount").getAsInt();
            int moveCount = gameFromServer.get("moveCounter").getAsInt();
            int step = gameFromServer.get("step").getAsInt();
            String phaseName = gameFromServer.get("phase").getAsString();
            boolean stepMode = gameFromServer.get("stepMode").getAsBoolean();

            System.out.println("Game Status:");
            System.out.println("Number of Players: " + numberOfPlayers);
            System.out.println("Number of Moves: " + moveCount);
            System.out.println("Number of steps: " + step);
            System.out.println("Game phase: " + phaseName);
            System.out.println("stepMode: " + stepMode);

        } else {
            System.out.println("Failed to connect");
        }

    }

    // (Ask if the game is finished)

    // todo - ask if the server needs gameId
    public boolean gameIsFinished() throws URISyntaxException, IOException, InterruptedException {
        URI gameIsFinishedURI = new URI(makeFullUri(ResourceLocation.gameStatus));

        Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(gameIsFinishedURI);

        boolean isGameFinished;
        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            isGameFinished = gameFromServer.get("isGameFinished").getAsBoolean();

            System.out.println(isGameFinished ? "The game is finished" : "The game is still running");
        } else {
            System.out.println("Failed to connect");
            return false;
        }
        return isGameFinished;
    }

    // (To get the clientId of the player, from the server)
    // todo - is it needed???
    public int getPlayerId() throws URISyntaxException, IOException, InterruptedException {
        URI gameURI = new URI(makeFullUri(ResourceLocation.joinGame));

        Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(gameURI);

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            JsonObject gameFromServer = jsonGameFromServer.getItem();

            playerId = gameFromServer.get("playerId").getAsInt();

            System.out.println("Players Id:" + playerId);

        } else {
            System.out.println("Failed to connect");
        }

        return playerId;
    }

    // (save game)
    public boolean saveGame() throws URISyntaxException, IOException, InterruptedException {
        // If the player is the first in the list, then the player is host
//        if (playerId == game.getPlayer(0).getPlayerID()) {
        URI savegameURI = new URI(makeFullUri(ResourceLocation.saveGame));

        // JsonObject object = new JsonObject();
        // object.add("game",game.serialize());

        Response<JsonObject> jsonGameFromServer = RequestMaker.postRequestJson(savegameURI, game.serialize());

        boolean savedSuccesfully = false;

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            System.out.println("Game " + gameId + " is saved");
            savedSuccesfully = true;
        } else {
            System.out.println("Failed to connect");
            System.out.println(jsonGameFromServer.getStatusCode());
        }
        return savedSuccesfully;
//        }
        /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cannot save game");
        alert.setHeaderText("Only the host can save the game");
        alert.setContentText("You don't have permission to save the game");
        alert.showAndWait();
*/
    }

    // (load game)
    public Game loadGame(int gameId) throws URISyntaxException, IOException, InterruptedException {

        URI loadgameURI = RequestMaker.makeUri(makeFullUri(ResourceLocation.saveGame), "gameId", gameId + "");

        Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(loadgameURI);

        if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
            System.out.println("Game from server: " + jsonGameFromServer.getItem().toString());
            JsonObject gameControllerFromServer = jsonGameFromServer.getItem();

            Game gameDeserializer = new OnlineGame(new Board(1, 1), 0);
            OnlineGame initialGame = (OnlineGame)gameDeserializer.deserialize(gameControllerFromServer.getAsJsonObject().get("game").getAsJsonObject());

            if (initialGame.getGameId() > 0) {
                this.gameId = initialGame.getGameId();
                this.playerId = gameControllerFromServer.get("playerId").getAsInt();
                initialGame.setClient(this);
                System.out.println("Succesfully loaded game");

                URI statusUri;
                try {
                    statusUri = RequestMaker.makeUri(makeFullUri(ResourceLocation.specificGame), getIdentification());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Status uri: " + statusUri);

                Response<JsonObject> currentStatus = RequestMaker.getRequestJson(statusUri);

                initialGame.setLocalPlayer(currentStatus.getItem().get("playerCount").getAsInt() - 1);
                this.game = initialGame;
                return game;
            }
            // gameId == 0 means the game is full : gameID == -1 means that the game doesn't exist
            System.out.println(playerId == 0 ? "Game is full" : "Game doesn't exist");
            return game;
        } else {
            System.out.println("Failed to connect");
        }

        return null;
    }

    /**
     * Deletes a saved remote game
     * @param gameId
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     * @author ZeeDiazz & Daniel
     */
    public boolean deleteSavedGame(int gameId) throws URISyntaxException, IOException, InterruptedException {
        URI deleteGameURI = RequestMaker.makeUri(makeFullUri(ResourceLocation.saveGame), "gameId", gameId+"");

        Response<String> response = RequestMaker.deleteRequest(deleteGameURI);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Succesfully deleted game (id: " + gameId + ")");
        }
        else {
            System.out.println("Did not delete game (id: " + gameId + ")");
        }
        return response.getStatusCode().is2xxSuccessful();
    }

    public void deleteActiveGame() throws URISyntaxException, IOException, InterruptedException {
        RequestMaker.deleteRequest(RequestMaker.makeUri(makeFullUri(ResourceLocation.specificGame), getIdentification()));

        this.gameId = -1;
        this.game = null;
        this.playerId = -1;
        this.playerIndex = -1;
    }

    private String makeFullUri(String relativeDestination) {
        return baseLocation + relativeDestination;
    }

    public boolean gameIsReady() {
        return this.game != null;
    }

    public Game getGame() {
        return this.game;
    }

    private Game deserializeGameFromServer(JsonObject gameInfo) {
        System.out.println("Info:\n" + gameInfo.entrySet());
        String boardName = gameInfo.get("boardName").getAsString();
        Board board;
        try {
            board = boardName.equals("RiskyCrossing") ? MapMaker.makeJsonRiskyCrossing() : MapMaker.makeJsonDizzyHighway();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int playerCount = gameInfo.get("playerCount").getAsInt();

        OnlineGame deserializedGame = new OnlineGame(board, playerCount);
        for (int i = 0; i < playerCount; i++) {
            Player player;
            if (i == playerIndex) {
                player = new LocalPlayer(deserializedGame, PLAYER_COLORS.get(i), "Player " + (i + 1));
            } else {
                player = new OnlinePlayer(deserializedGame, PLAYER_COLORS.get(i), "Player " + (i + 1));
            }
            deserializedGame.addPlayer(player);
        }
        deserializedGame.setGameId(this.gameId);
        deserializedGame.setClient(this);
        return deserializedGame;
    }

    public void startGame() {
        URI statusUri;
        try {
            statusUri = new URI(makeFullUri(ResourceLocation.gameStatus));
        } catch (URISyntaxException e) {
            // TODO handle
            throw new RuntimeException(e);
        }

        System.out.println("Sending start update");
        JsonObject object = new JsonObject();
        object.addProperty("gameId", gameId);
        object.addProperty("playerId", playerId);
        object.addProperty("startGame", true);

        try {
            System.out.println("Sending POST request");
            RequestMaker.postRequest(statusUri, object.toString());
        } catch (IOException | InterruptedException e) {
            // TODO handle
            throw new RuntimeException(e);
        }
        System.out.println("Finished startGame()");

        JsonObject gameInfo;
        try {
            URI gameUri = RequestMaker.makeUri(this.makeFullUri(ResourceLocation.specificGame), getIdentification());
            gameInfo = RequestMaker.getRequestJson(gameUri).getItem();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        game = deserializeGameFromServer(gameInfo);
    }

    private Map<String, String> getIdentification() {
        Map<String, String> identification = new HashMap<>();

        identification.put("gameId", gameId + "");
        identification.put("playerId", playerId + "");

        return identification;
    }

    public void loadMoves() {
        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
            String playerMovesString = jsonMovesArray.get(i).getAsString().replace("[", "").replace("]", "").replace("\"", "");
            System.out.println("Player moves string: " + playerMovesString);
            String[] playerMoveStrings = playerMovesString.split(",");
            for (int j = 0; j < playerMoveStrings.length; j++) {
                String commandString = playerMoveStrings[j];
                System.out.println("Command string: " + commandString);
                CommandCard card;
                if (commandString.equals("")) {
                    card = null;
                    System.out.println("No command");
                } else {
                    card = new CommandCard(Command.valueOf(commandString));
                    System.out.println("Command: " + card.command);
                }
                player.getProgramField(j).setCard(card);
            }
        }
    }

    public void saveInteraction(Command latestOption) throws IOException, InterruptedException, URISyntaxException {
        URI finishedProgrammingPhaseURI = new URI(makeFullUri(ResourceLocation.gameStatus));

        JsonObject request = new JsonObject();
        request.addProperty("gameId", gameId);
        request.addProperty("playerId", playerId);
        request.addProperty("interaction", latestOption.toString());

        Response<String> jsonProgrammingPhase = RequestMaker.postRequest(finishedProgrammingPhaseURI, request);

        if (jsonProgrammingPhase.getStatusCode().is2xxSuccessful()) {
            System.out.println("Successfully sent the interaction");
        } else {
            System.out.println("Failed to connect");
        }
    }

    public Command getInteraction(int interactionsMade) throws IOException, InterruptedException, URISyntaxException {
        URI interactionUri = RequestMaker.makeUri(makeFullUri(ResourceLocation.gameStatus), getIdentification());

        JsonArray interactionsJson = null;
        boolean hasInteraction = false;
        while (!hasInteraction) {
            Response<JsonObject> jsonGameFromServer = RequestMaker.getRequestJson(interactionUri);

            if (jsonGameFromServer.getStatusCode().is2xxSuccessful()) {
                JsonObject gameFromServer = jsonGameFromServer.getItem();

                interactionsJson = gameFromServer.get("interactions").getAsJsonArray();
                hasInteraction = interactionsJson.size() > interactionsMade;
            }
        }
        return Command.valueOf(interactionsJson.get(interactionsMade).getAsString());
    }

    public int getGameId() {
        return this.gameId;
    }

}
