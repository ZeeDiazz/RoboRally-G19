package dk.dtu.compute.se.pisd.roborally.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Board;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.MapMaker;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.OnlineGame;
import dk.dtu.compute.se.pisd.roborally.restful.JsonResponseMaker;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import dk.dtu.compute.se.pisd.roborally.restful.ResponseMaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is the RestController for the server.
 * @auther Felix Schmidt (Felix723)
 * @auther Daniel Jensen
 */

@RestController
public class Server {

    // intialize list of lobbies, not null
    private final List<Lobby> lobbies = new ArrayList<>();
    private final Random rng = new Random();
    private final static String databasePath = "src/main/resources/database/";
    private final static JsonParser jsonParser = new JsonParser();
    private final static ResponseMaker<Void> emptyResponseMaker = new ResponseMaker<>();
    private final static JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
    private ResponseMessage responseMessages;
    String filePath = "src/main/resources/games";
    String filePath2 = "src/main/resources/boards/5B.json";
    String defaultPath = "src/main/resources/games/";
    String saveboardpath = "src/main/resources/games/";
    File file = new File(filePath);
    File file2 = new File(filePath2);

    public Server() {

    }

    private int makeNewLobbyId() {
        int id;
        boolean takenId;
        do {
            id = rng.nextInt(0, Integer.MAX_VALUE);
            takenId = false;

            for (Lobby lobby : lobbies) {
                if (lobby.hasPlayer(id)) {
                    takenId = true;
                    break;
                }
            }
        } while (takenId);
        return id;
    }

    private int getLobbyIndex(int lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).getId() == lobbyId) {
                return i;
            }
        }
        return -1;
    }

    private boolean lobbyExists(int lobbyId) {
        return getLobbyIndex(lobbyId) != -1;
    }

    private Lobby getLobby(int lobbyId) {
        int index = getLobbyIndex(lobbyId);
        if (index == -1) {
            return null;
        }
        return lobbies.get(index);
    }
    private boolean notEnoughInfo(JsonObject jsonObject) {
        return !jsonObject.has("gameId") || !jsonObject.has("playerId");
    }

    /**
     * This method handles a get request to the server at the location /games
     * @return an item response with a list of all the games
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */
    @GetMapping(ResourceLocation.allGames)
    public ResponseEntity<String> getAllGames() {
        JsonObject response = new JsonObject();
        JsonArray ids = new JsonArray();
        for (Lobby lobby : lobbies) {
            ids.add(lobby.getId());
        }

        return responseMaker.itemResponse(response);
    }

    /***
     * This method handles a get request to the server at the location /game
     * @param gameId, an integer value representing the id of the game
     * @return an item response with the game info
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */
    @GetMapping(ResourceLocation.specificGame)
    public ResponseEntity<String> getGameInfo(@RequestParam Integer gameId) {
        JsonObject response = new JsonObject();
        if(gameId == null) {
            return responseMaker.forbidden();
        }
        Lobby lobby = getLobby(gameId);
        if (lobby != null) {
            response.addProperty("gameId", gameId);
            response.addProperty("playerCount", lobby.getPlayerCount());
            response.addProperty("boardName", lobby.getBoardName());
            List<Integer> players = lobby.getPlayerIds();
            response.add("players", (new JsonParser()).parse(players.toString()));
            return responseMaker.itemResponse(response);
            // TODO: return a more game info (as JSON)

        }
        return responseMaker.notFound();

    }
    /* -- resource /game -- */

    /**
     * This method handles a post request to the server at the location /game
     * @param stringInfo a string containing the info about the game
     * @return a response with the game info and http status code
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */

    @PostMapping(ResourceLocation.specificGame)
    public ResponseEntity<String> lobbyCreateRequest(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        if(!info.has("minimumPlayers") || !info.has("boardName")) {
            return responseMaker.forbidden();
        }
        int minimumPlayers = info.get("minimumPlayers").getAsInt();

        int lobbyId = -1;
        boolean needNewId;
        if (info.has("gameId")) {
            lobbyId = info.get("gameId").getAsInt();
            System.out.println("Requested lobby id: " + lobbyId);

            needNewId = lobbyExists(lobbyId);
        }
        else {
            System.out.println("Didn't specify a lobby id");

            needNewId = true;
        }

        if (needNewId) {
            // Get a lobby id that we don't already use
            lobbyId = makeNewLobbyId();
        }

        Lobby lobby = new Lobby(lobbyId, minimumPlayers, info.get("boardName").getAsString());
        lobbies.add(lobby);

        int playerId = lobby.makePlayerId();
        lobby.addPlayer(playerId);

        JsonObject response = new JsonObject();
        response.addProperty("gameId", lobbyId);
        response.addProperty("playerId", playerId);
        response.addProperty("playerIndex", 0);

        return responseMaker.created(response.toString());
        /*
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyCreatedMessage(lobbyId));
         */
    }

    /**
     * This method handles a delete request to the server at the location /game
     * @param gameId
     * @param playerId
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */
    @DeleteMapping(ResourceLocation.specificGame)
    public ResponseEntity<Void> deleteActiveGame(@RequestParam Integer gameId, @RequestParam Integer playerId) {
        Lobby lobby = getLobby(gameId);

        if (lobby == null) {
            return emptyResponseMaker.notFound();
        }

        if (!lobby.hasPlayer(playerId) || !lobby.isHost(playerId)) {
            return emptyResponseMaker.forbidden();
        }
        lobbies.remove(lobby);
        return emptyResponseMaker.ok();
    }

    /**
     * This method handles a post request to the server at the location /game/join
     * @param stringInfo
     * @return a response with the game info and http status code
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */
    @PostMapping(ResourceLocation.joinGame)
    public ResponseEntity<String> playerJoinRequest(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        int lobbyId = info.get("gameId").getAsInt();

        System.out.println("Player trying to join lobby " + lobbyId);
        // get the lobby with matching id
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {
            return responseMaker.notFound();
        }

        int playersInLobby = lobby.getPlayerCount();
        if (playersInLobby >= 6) {
            return responseMaker.forbidden();
        }
        // add player to lobby
        int playerId = lobby.makePlayerId();
        System.out.println("Player given id: " + playerId);
        // Add player to the lobby
        lobby.addPlayer(playerId);

        JsonObject response = new JsonObject();
        response.addProperty("playerId", playerId);
        response.addProperty("playerIndex", lobby.getPlayerCount() - 1);
        return responseMaker.itemResponse(response);
    }

    /**
     * This method handles a post request to the server at the location /game/leave
     * @param stringInfo
     * @return a response with the game info and http status code
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */
    @PostMapping(ResourceLocation.leaveGame)
    public ResponseEntity<Void> playerLeaveRequest(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        // check if lobby and player exists
        if (!info.has("gameId")) {
            return emptyResponseMaker.methodNotAllowed();
        } else if (!info.has("playerId")) {
            return emptyResponseMaker.unauthorized();
        }

        int lobbyId = info.get("gameId").getAsInt();
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {
            return emptyResponseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        if (!lobby.hasPlayer(playerId)) {
            return emptyResponseMaker.forbidden();
        }

        lobby.removePlayer(playerId);
        return emptyResponseMaker.ok();
    }

    /**
     * This method handles a get reqeust to the server at the location /game/status
     * @param gameId
     * @param playerId
     * @return a response with the game status and http status code
     * @auther Felix Schmidt (Felix723)
     * @auther Daniel Jensen
     */
    @GetMapping(ResourceLocation.gameStatus)
    public ResponseEntity<String> getGameStatus(@RequestParam Integer gameId, @RequestParam(required = false) Integer playerId) {
        JsonObject response = new JsonObject();
        Lobby lobby = getLobby(gameId);
        if (lobby != null) {
            response.addProperty("stepCount", lobby.getStepsTaken());
            response.addProperty("hasStarted", lobby.isActive());
            response.addProperty("canLaunch", lobby.canLaunch());
            response.addProperty("isReady", lobby.isReady());
            JsonArray interactions = new JsonArray();
            for (String interaction : lobby.getInteractions()) {
                interactions.add(interaction);
            }
            response.add("interactions", interactions);

            if (lobby.isReady()) {
                JsonArray moves = new JsonArray();
                for (String move : lobby.getLatestMoves()) {
                    moves.add(move);
                }
                response.add("moves", moves);

                if (playerId != null && lobby.hasPlayer(playerId)) {
                    lobby.hasRetrievedInfo(playerId);
                    if (lobby.allHaveInfo()) {
                        lobby.resetReadyStatus();
                        lobby.resetMoves();
                        lobby.resetInteractions();
                    }
                }
            }
            return responseMaker.itemResponse(response);
        }
        return responseMaker.notFound();
    }

    /**
     * This method handles a post request to the server at the location /game/status
     * @param stringInfo
     * @return a response with the game status and http status code
     * @auther Felix Schmidt (Felix723)
     */
    @PostMapping(ResourceLocation.gameStatus)
    public ResponseEntity<Void> updateGameStatus(@RequestBody String stringInfo) {
        System.out.println("Player sent update: " + stringInfo);
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);

        if (!info.has("gameId")) {
            return emptyResponseMaker.methodNotAllowed();
        } else if (!info.has("playerId")) {
            return emptyResponseMaker.unauthorized();
        }

        int lobbyId = info.get("gameId").getAsInt();
        Lobby lobby = getLobby(lobbyId);
        if (lobby == null) {
            return emptyResponseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        if (!lobby.hasPlayer(playerId)) {
            return emptyResponseMaker.forbidden();
        }

        if (info.has("startGame") && info.get("startGame").getAsBoolean()) {
            lobby.setActive();
        }
        if (info.has("moves")) {
            System.out.println("Player sent moves: " + info.get("moves").toString());
            lobby.updateMoves(playerId, info.get("moves").toString());
        }
        if (info.has("isReady")) {
            boolean playerIsReady = info.get("isReady").getAsBoolean();
            lobby.setReadyStatus(playerId, playerIsReady);
            System.out.println("Player is ready: " + playerIsReady);
            System.out.println("Lobby is ready: " + lobby.isReady() + " (" + lobby.getReadyCount() + "/" + lobby.getPlayerCount() + ")");
        }
        if (info.has("interaction")) {
            lobby.addInteraction(info.get("interaction").getAsString());
        }
        return emptyResponseMaker.ok();
    }

    /**
     * this method handles a get request to the server at the location /game/lobby
     * @param gameId
     * @return a response with the game and http status code
     * @auther Zahedullah Wafa
     */
    @GetMapping(ResourceLocation.saveGame)
    public ResponseEntity<String> loadGame(@RequestParam Integer gameId) {
        String filename = databasePath + gameId + ".json";
        File file = new File(filename);

        if (!file.exists()) {
            return responseMaker.notFound();
        }
        if (gameId == null) {
            return responseMaker.methodNotAllowed();
        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonObject loadedInfo = jsonParser.parse(fileReader).getAsJsonObject();

            Lobby lobby;
            if (!lobbyExists(gameId)) {
                lobby = new Lobby(gameId, loadedInfo.get("playerCount").getAsInt(), loadedInfo.get("board").getAsJsonObject().get("boardName").getAsString());
                lobby.setActive();
                lobbies.add(lobby);
            }
            else {
                lobby = getLobby(gameId);
            }
            int playerId = lobby.makePlayerId();
            lobby.addPlayer(playerId);

            JsonObject loadedGame = new JsonObject();
            loadedGame.addProperty("playerId", playerId);
            loadedGame.add("game", loadedInfo);
            return responseMaker.itemResponse(loadedGame);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this method handles a post request to the server at the location /game/lobby
     * @param stringInfo
     * @return an empty response json object with the http status code
     * @throws IOException
     * @auther Felix Schmidt (Felix723)
     */

    @PostMapping(ResourceLocation.saveGame)
    public ResponseEntity<String> saveGame(@RequestBody String stringInfo) throws IOException {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        if(!info.has("gameId")) {
            return responseMaker.methodNotAllowed();
        }
        String pathVariable = info.get("gameId").getAsString();
        String filename = databasePath + pathVariable + ".json";

        if(Files.deleteIfExists(Paths.get(filename))){
            System.out.println("Deleted old file");
        }
        File file = new File(filename);
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stringInfo);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error while saving game");
            throw new RuntimeException(e);
        }
        JsonObject response = new JsonObject();
        return responseMaker.itemResponse(response);
    }

    /**
     * this method handles a delete request to the server at the location /game/lobby
     * @param stringInfo the game id
     * @auther Felix Schmidt (Felix723)
     */
    @DeleteMapping(ResourceLocation.saveGame)
    public ResponseEntity<Void> deleteSavedGame(@RequestParam Integer gameId) {
        String fileName = databasePath + gameId + ".json";
        System.out.println("Deleting file at " + fileName);
        try {
            boolean success = Files.deleteIfExists(Paths.get(fileName));
            if (success) {
                return emptyResponseMaker.ok();
            } else {
                return emptyResponseMaker.notFound();
            }
        } catch (IOException e) {
            System.out.println("Error while deleting game");
            throw new RuntimeException(e);
        }
    }
    /**
     *
     * @param file
     * @return
     * @auther Felix Schmidt (Felix732)
     */
    public JsonObject makeJsonObject(File file) {
        JsonParser parser = new JsonParser();
        try {
            return (JsonObject) parser.parse(new FileReader(file));
        } catch (Exception e) {
            System.out.println("Error while reading file");
            return null;
        }
    }
    /**
     *
     * @param jsonObject
     * @auther Felix Schmidt (Felix732)
     */
    public void makeFileFromJsonObject (JsonObject jsonObject){
        int gamId = jsonObject.get("gameId").getAsInt();
        File file = new File(saveboardpath+gamId+".json");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error while writing file");
        }
    }
    JsonObject notNullJsonObject(JsonObject jsonObject){
        jsonObject = new JsonObject();
        jsonObject.addProperty("lobbyId", 1);
        jsonObject.addProperty("playerId", -1);
        jsonObject.addProperty("boardName", "RiskyCrossing");
        jsonObject.addProperty("numberOfPlayers", 2);
        return jsonObject;
    }
}
