package dk.dtu.compute.se.pisd.roborally.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
 * @auther Felix Schmidt (Felix723)
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


    @GetMapping(ResourceLocation.allGames)
    public ResponseEntity<String> getAllGames() {
        JsonObject response = new JsonObject();
        JsonArray ids = new JsonArray();
        for (Lobby lobby : lobbies) {
            ids.add(lobby.getId());
        }

        return responseMaker.itemResponse(response);
    }

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

    @PostMapping(ResourceLocation.specificGame)
    public ResponseEntity<String> lobbyCreateRequest(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        if(!info.has("gameId") || !info.has("minimumPlayers") || !info.has("boardName")) {
            return responseMaker.forbidden();
        }
        int lobbyId = info.get("gameId").getAsInt();
        int minimumPlayers = info.get("minimumPlayers").getAsInt();

        System.out.println("Requested lobby id: " + lobbyId);
        // Make a random id that we don't already use
        while (lobbyExists(lobbyId)) {
            lobbyId = makeNewLobbyId();
        }

        Lobby lobby = new Lobby(lobbyId, minimumPlayers, info.get("boardName").getAsString());
        lobbies.add(lobby);

        int playerId = lobby.makePlayerId();
        lobby.addPlayer(playerId);

        JsonObject response = new JsonObject();
        response.addProperty("gameId", lobbyId);
        response.addProperty("playerId", playerId);

        return responseMaker.created(response.toString());
        /*
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyCreatedMessage(lobbyId));
         */
    }

    @DeleteMapping(ResourceLocation.specificGame)
    public ResponseEntity<Void> deleteActiveGame(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);

        if(notEnoughInfo(info)) {
            return emptyResponseMaker.forbidden();
        }

        int lobbyId = info.get("gameId").getAsInt();
        Lobby lobby = getLobby(lobbyId);

        if (lobby == null) {
            return emptyResponseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();

        if (!lobby.hasPlayer(playerId) || !lobby.isHost(playerId)) {
            return emptyResponseMaker.forbidden();
        }
        return emptyResponseMaker.ok();
    }
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

        return responseMaker.itemResponse(response.toString());
    }

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

    @GetMapping(ResourceLocation.gameStatus)
    public ResponseEntity<String> getGameStatus(@RequestParam Integer gameId, @RequestParam(required = false) Integer playerId) {
        JsonObject response = new JsonObject();
        Lobby lobby = getLobby(gameId);
        if (lobby != null) {
            response.addProperty("stepCount", lobby.getStepsTaken());
            response.addProperty("hasStarted", lobby.isActive());
            response.addProperty("canLaunch", lobby.canLaunch());
            response.addProperty("isReady", lobby.isReady());

            if (lobby.isReady()) {
                JsonArray moves = new JsonArray();
                for (String move : lobby.getLatestMoves()) {
                    moves.add(move);
                }
                response.add("moves", moves);
            }

            if (playerId != null && lobby.hasPlayer(playerId)) {
                lobby.hasRetrievedInfo(playerId);
                if (lobby.allHaveInfo()) {
                    lobby.resetReadyStatus();
                    lobby.resetMoves();
                }
            }
            return responseMaker.itemResponse(response);
        }
        return responseMaker.notFound();
    }

    @PostMapping(ResourceLocation.gameStatus)
    public ResponseEntity<Void> updateGameStatus(@RequestBody String stringInfo) {
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
            lobby.updateMoves(playerId, info.get("moves").getAsString());
        }
        if (info.has("isReady")) {
            boolean playerIsReady = info.get("isReady").getAsBoolean();
            lobby.setReadyStatus(playerId, playerIsReady);
        }
        return emptyResponseMaker.ok();
    }

    @GetMapping(ResourceLocation.saveGame)
    public ResponseEntity<String> loadGame(@RequestParam Integer gameId) {
        String filename = databasePath + gameId + ".json";
        File file = new File(filename);

        if (!file.exists()) {
            return responseMaker.notFound();
        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonObject loadedInfo = jsonParser.parse(fileReader).getAsJsonObject();

            return ResponseEntity.ok(loadedInfo.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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

    @DeleteMapping(ResourceLocation.saveGame)
    public ResponseEntity<Void> deleteSavedGame(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        if (!info.has("gameId")) {
            return emptyResponseMaker.methodNotAllowed();
        }
        String gameId = info.get("gameId").getAsString();
        String fileName = databasePath + gameId + ".json";
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
}
