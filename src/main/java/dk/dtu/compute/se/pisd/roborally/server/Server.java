package dk.dtu.compute.se.pisd.roborally.server;

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
import java.util.concurrent.atomic.AtomicLong;

/**
 * @auther Felix Schmidt (Felix723)
 */

@RestController
public class Server {
    // TODO: 2023-06-08 implement a way to get ALL games
    // TODO: 2023-06-08 implement a way to get info on specific game
    // TODO: 2023-06-08 implement a way to get info on specific player
    // TODO: 2023-06-09 implement a way to Load game, save game, delete saved game

    // intialize list of lobbies, not null
    private final List<Lobby> lobbies = new ArrayList<>();
    private final Random rng = new Random();
    private final String databasePath = "src/main/resources/database/";
    private final static JsonParser jsonParser = new JsonParser();

    public Server() {

    }

    private int makeNewLobbyId() {
        int id;
        boolean takenId;
        do {
            id = rng.nextInt(0, Integer.MAX_VALUE);
            takenId = false;

            for (Lobby lobby : lobbies) {
                if (lobby.getId() == id) {
                    takenId = true;
                    break;
                }
            }
        } while (takenId);
        return id;
    }

    private int makeNewPlayerId(Lobby lobby) {
        int id;
        boolean takenId;
        do {
            id = rng.nextInt(0, Integer.MAX_VALUE);
            takenId = lobby.hasPlayer(id);
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
        return !jsonObject.has("lobbyId") || !jsonObject.has("playerId");
    }


    @GetMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return (new ResponseMaker<String>()).ok();
    }

    @GetMapping(ResourceLocation.allGames)
    public ResponseEntity<Integer[]> getAllGames() {
        ResponseMaker<Integer[]> responseMaker = new ResponseMaker<>();

        Integer[] ids = new Integer[lobbies.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = lobbies.get(i).getId();
        }

        return responseMaker.itemResponse(ids);
    }

    @GetMapping(ResourceLocation.specificGame)
    public ResponseEntity<String> getGameInfo(@RequestParam Integer lobbyId) {
        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
        JsonObject response = new JsonObject();
        if(lobbyId == null){
            return responseMaker.forbidden();
        }
        if (lobbyExists(lobbyId)) {
            response.addProperty("lobbyId", lobbyId);
            List players = getLobby(lobbyId).getPlayerIds();
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

        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
        Random rng = new Random();
        if(!info.has("gameId") || !info.has("minimumPlayers")){
            return responseMaker.forbidden();
        }
        int lobbyId = info.get("gameId").getAsInt();
        int minimumPlayers = info.get("minimumPlayers").getAsInt();

        System.out.println("Requested lobby id: " + lobbyId);
        // Make a random id that we don't already use
        while (lobbyExists(lobbyId)) {
            lobbyId = makeNewLobbyId();
        }
        lobbies.add(new Lobby(lobbyId));
        int lobbyIndex = lobbies.size() - 1;
        long playerId = lobbies.get(lobbyIndex).givePlayerId();
        lobbies.get(lobbyIndex).addPlayer((int)playerId);
        lobbies.get(lobbyIndex).setMinimumPlayers(minimumPlayers);

        JsonObject response = new JsonObject();
        response.addProperty("gameId", lobbyId);

        return responseMaker.created(response.toString());
        /*
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyCreatedMessage(lobbyId));
         */
    }

    @DeleteMapping(ResourceLocation.specificGame)
    public ResponseEntity<Void> deleteActiveGame(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();

        if(notEnoughInfo(info)) {
            return responseMaker.forbidden();
        }

        int lobbyId = info.get("lobbyId").getAsInt();
        Lobby lobby = null;
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).getId() == lobbyId) {
                lobby = lobbies.get(i);
                break;
            }
        }

        if (lobby == null) {
            return responseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        boolean playerInLobby = false;
        for (Integer lobbyPlayer : lobby.getPlayerIds()) {
            if (lobbyPlayer == playerId) {
                playerInLobby = true;
                break;
            }
        }

        if (!playerInLobby || lobby.getPlayerIds().get(0) != playerId) {
            return responseMaker.forbidden();
        }
        return responseMaker.ok();
    }
    @PostMapping(ResourceLocation.joinGame)
    public ResponseEntity<String> playerJoinRequest(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        int lobbyId = info.get("gameId").getAsInt();

        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
        System.out.println("Player trying to join lobby " + lobbyId);
        // get the lobby with matching id
        int playersInLobby = getLobby(lobbyId).getNumberOfPlayers();
        if(playersInLobby >= 6){
            return responseMaker.forbidden();
        }
        // add player to lobby
        Lobby currentLobby = getLobby(lobbyId);
        int playerId = (int)getLobby(lobbyId).givePlayerId();
        System.out.println("Player given id: " + playerId);
        // Add player to the lobby
        getLobby(lobbyId).addPlayer(playerId);

        JsonObject response = new JsonObject();
        response.addProperty("playerId", playerId);

        return responseMaker.itemResponse(response.toString());
    }

    @PostMapping(ResourceLocation.leaveGame)
    public ResponseEntity<Void> playerLeaveRequest(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();
        // check if lobby and player exists
        if (!info.has("lobbyId")) {
            return responseMaker.methodNotAllowed();
        } else if (!info.has("playerId")) {
            return responseMaker.unauthorized();
        }

        int lobbyId = info.get("lobbyId").getAsInt();
        Lobby lobby = null;
        for (Lobby l : lobbies) {
            if (l.getId() == lobbyId) {
                lobby = l;
                break;
            }
        }

        if (lobby == null) {
            return responseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        boolean playerInLobby = false;
        for (Integer lobbyPlayer : lobby.getPlayerIds()) {
            if (lobbyPlayer == playerId) {
                playerInLobby = true;
                break;
            }
        }

        if (!playerInLobby) {
            return responseMaker.forbidden();
        }

        lobby.removePlayer(playerId);
        return responseMaker.ok();
    }

    @GetMapping(ResourceLocation.gameStatus)
    public ResponseEntity<String> getGameStatus(@RequestParam Integer lobbyId) {
        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
        JsonObject response = new JsonObject();
        Lobby lobby = getLobby(lobbyId);
        if (lobbyExists(lobbyId)) {
            // TODO make JSON
            if(lobby.canLaunch()) {
                if (lobby.isReady()) {
                    response.addProperty("status", "ready");
                } else {
                    response.addProperty("status", "not ready");
                }
                return responseMaker.itemResponse(response);
            } else {
                response.addProperty("status", "waiting");
                return responseMaker.itemResponse(response);
            }
        }
        return responseMaker.notFound();
    }

    @PostMapping(ResourceLocation.gameStatus)
    public ResponseEntity<Void> updateGameStatus(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();

        if (!info.has("lobbyId") || !info.has("status")) {
            return responseMaker.methodNotAllowed();
        } else if (!info.has("playerId")) {
            return responseMaker.unauthorized();
        }

        int lobbyId = info.get("lobbyId").getAsInt();
        Lobby lobby = null;
        for (Lobby l : lobbies) {
            if (l.getId() == lobbyId) {
                lobby = l; //
                break;
            }
        }

        if (lobby == null) {
            return responseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        boolean playerInLobby = false;
        for (Integer lobbyPlayer : lobby.getPlayerIds()) {
            if (lobbyPlayer == playerId) {
                playerInLobby = true;
                break;
            }
        }

        if (!playerInLobby) {
            return responseMaker.forbidden();
        }

        boolean playerIsReady = info.get("status").getAsBoolean();
        if (playerIsReady) {
            lobby.setIsReady(playerId);
        } else {
            lobby.setNotReady(playerId);
        }
        return responseMaker.ok();
    }

    @GetMapping(ResourceLocation.saveGame)
    public ResponseEntity<String> loadGame(@RequestParam Integer gameId) {
        String filename = databasePath + gameId + ".json";
        File file = new File(filename);
        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();

        if (!file.exists()) {
            return responseMaker.notFound();

        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonObject loadedInfo = jsonParser.parse(fileReader).getAsJsonObject();

            return ResponseEntity.ok(loadedInfo.toString());

        } catch (IOException e) {
            System.out.println("Error while loading game");
            throw new RuntimeException(e);
        }
    }


    @PostMapping(ResourceLocation.saveGame)
    public ResponseEntity<Void> saveGame(@RequestBody String stringInfo) throws IOException {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();
        if(!info.has("gameId")){
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
        return responseMaker.ok();
    }

    @DeleteMapping(ResourceLocation.saveGame)
    public ResponseEntity<Void> deleteSavedGame(@RequestBody String stringInfo) {
        JsonObject info = (JsonObject)jsonParser.parse(stringInfo);
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();
        if(!info.has("gameId")){
            return responseMaker.methodNotAllowed();
        }
        String pathVariable = info.get("gameId").getAsString();
        String fileName = databasePath + pathVariable + ".json";
        try{
            boolean success = Files.deleteIfExists(Paths.get(fileName));
            if (success){
                return responseMaker.ok();
            }else {
                return responseMaker.notFound();
            }
        } catch (IOException e) {
            System.out.println("Error while deleting game");
            throw new RuntimeException(e);
        }
    }
}
