package dk.dtu.compute.se.pisd.roborally.server;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.List;
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

    private ResponseMessage responseMessages;
    String filePath = "src/main/resources/boards/5A.json";
    String filePath2 = "src/main/resources/boards/5B.json";
    String filePath3 = "src/main/resources/games/1.json";
    String defaultPath = "src/main/resources/games/";
    File file = new File(filePath);
    File file2 = new File(filePath2);
    File file3 = new File(filePath3);

    public Server() {

    }

    ObjectMapper objectMapper = new ObjectMapper();

    // intialize list of lobbies, not null
    List<Lobby> lobbies = new ArrayList<>();
    private int lobbySize = 0;
    private final AtomicLong counter = new AtomicLong();
    private final AtomicLong lobbyCounter = new AtomicLong();


    /**
     * Method for handling get requests to /greeting
     *
     * @param name a string value
     * @return a Greeting object using the counter and template
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return (new ResponseMaker<String>()).ok();
    }

    @GetMapping(ResourceLocation.allGames)
    public ResponseEntity<Integer[]> getAllGames() throws IOException {
        ResponseMaker<Integer[]> responseMaker = new ResponseMaker<>();

        Integer[] ids = new Integer[lobbies.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = lobbies.get(i).getLobbyId();
        }

        return responseMaker.itemResponse(ids);
    }

    /* -- resource /game -- */
    @GetMapping(ResourceLocation.specificGame)
    public ResponseEntity<String> getGameInfo(@RequestParam Integer lobbyId) throws IOException {
        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
        //int lobbyId = info.get("lobbyId").getAsInt();
        System.out.println("trying to make jsonobject");
        JsonObject item = new JsonObject();
        item.addProperty("lobbyId", lobbyId);
        System.out.println("added property");
        return responseMaker.itemResponse(makeJsonObject(file2));
    }

    /**
     * Method for handling post requests to /api/lobby/create
     *
     * @param lobbyId Integer value to identify the lobby
     * @return a Greeting object using the counter and lobbyCreated template
     * @author Felix Schmidt (Felix732) & Daniel Jensen
     */
    @PostMapping(ResourceLocation.specificGame)
    public ResponseEntity<Integer> lobbyCreateRequest(@RequestBody(required = false) Integer lobbyId) throws IOException {
        System.out.println("1");
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();
        System.out.println("Requested lobby id: " + lobbyId);
        // Make a random id that we don't already use
        while (lobbyId == null || hasLobby(lobbyId)) {
            lobbyId = (int) lobbyCounter.incrementAndGet();
        }
        lobbies.add(new Lobby(lobbyId));
        System.out.println("Lobby added succes");

        return responseMaker.itemResponse(lobbyId);
        /*
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyCreatedMessage(lobbyId));
         */
    }

    @DeleteMapping(ResourceLocation.specificGame)
    public ResponseEntity<Void> deleteActiveGame(@RequestParam Integer lobbyId, @RequestParam(required = false) Integer playerId) {
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();
/*
        if (!info.has("lobbyId")) {
            return responseMaker.methodNotAllowed();
        } else if (!info.has("playerId")) {
            return responseMaker.unauthorized();
        }

        int lobbyId = info.get("lobbyId").getAsInt();*/
        Lobby lobby = null;
       /* for(int i = 0; i < lobbies.size(); i++){
            if(lobbies.get(i).Id == lobbyId){
                lobby = lobbies.get(i);
                break;
            }
        }*/
        for (Lobby l : lobbies) {
            if (l.getLobbyId() == lobbyId) {
                lobby = l;
                break;
            }
        }
        deleteLobby(lobbyId);
        if (lobby == null) {
            return responseMaker.notFound();
        }

        //int playerId = info.get("playerId").getAsInt();
        boolean playerInLobby = false;
        for (Integer lobbyPlayer : lobby.getPlayers()) {
            if (lobbyPlayer == playerId) {
                playerInLobby = true;
                break;
            }
        }

        if (!playerInLobby || lobby.getPlayers().get(0) != playerId) {
            return responseMaker.forbidden();
        }
        return responseMaker.ok();
    }

    /**
     * Method for handling post request to /api/lobby/join
     *
     * @param lobbyId Integer value to identify the lobby
     * @return a Greeting object using the counter and lobbyJoined template
     * @auther Felix Schmidt (Felix732)
     */
    @PostMapping(ResourceLocation.joinGame)
    public ResponseEntity<Integer> playerJoinRequest(@RequestParam Integer lobbyId) {
        System.out.println("Player trying to join lobby " + lobbyId);
        // get the lobby with matching id
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyId() == lobbyId) {
                // check if lobby is full
                if (lobby.getPlayers().size() >= 6) {
                    System.out.println("Lobby is full");
                    return (new ResponseMaker<Integer>()).forbidden();
                }
            }
        }
        // add player to lobby
        System.out.println("Lobby not full");
        int playerId = (int) counter.incrementAndGet();
        System.out.println("Player given id: " + playerId);
        addPlayerToLobby(playerId, lobbyId);
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();
        return responseMaker.itemResponse(playerId);
    }

    @PostMapping(ResourceLocation.leaveGame)
    public ResponseEntity<Void> playerLeaveRequest(@RequestBody JsonObject info) {
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
            if (l.getLobbyId() == lobbyId) {
                lobby = l;
                break;
            }
        }

        if (lobby == null) {
            return responseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        boolean playerInLobby = false;
        for (Integer lobbyPlayer : lobby.getPlayers()) {
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
    public ResponseEntity<Integer> getGameStatus(@RequestParam Integer lobbyId) {
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();
        if (hasLobby(lobbyId)) {
            return responseMaker.itemResponse(lobbyId);
        }
        return responseMaker.notFound();
    }

    @PostMapping(ResourceLocation.gameStatus)
    public ResponseEntity<Integer> updateGameStatus(@RequestBody JsonObject info) {
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();

        if (!info.has("lobbyId") || !info.has("status")) {
            return responseMaker.methodNotAllowed();
        } else if (!info.has("playerId")) {
            return responseMaker.unauthorized();
        }

        int lobbyId = info.get("lobbyId").getAsInt();
        Lobby lobby = null;
        for (Lobby l : lobbies) {
            if (l.getLobbyId() == lobbyId) {
                lobby = l;
                break;
            }
        }

        if (lobby == null) {
            return responseMaker.notFound();
        }

        int playerId = info.get("playerId").getAsInt();
        boolean playerInLobby = false;
        for (Integer lobbyPlayer : lobby.getPlayers()) {
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
    public ResponseEntity<String> loadGame(@RequestParam JsonObject info){
        System.out.println("Trying to load game");
        JsonResponseMaker<JsonObject> responseMaker = new JsonResponseMaker<>();
        /*boolean newGame = info.get("newGame").getAsBoolean();
        if (newGame) {
            return responseMaker.itemResponse(makeJsonObject(file3));
        }*/
        //int filePath = info.get("gameId").getAsInt();
        String filePath = info.get("lobbyId").getAsString();
        File filePathGiven = new File(defaultPath + filePath+".json");
        return responseMaker.itemResponse(makeJsonObject(filePathGiven));
    }
    @GetMapping(ResourceLocation.allGames)
    public ResponseEntity<String> getAllofTheGame(){
        ResponseMaker<String> responseMaker = new JsonResponseMaker<>();
        return responseMaker.ok();
    }

    @PostMapping(ResourceLocation.saveGame)
    public ResponseEntity<Void> saveGame(@RequestBody JsonObject game) {
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();
        return responseMaker.notImplemented();
    }

    @DeleteMapping(ResourceLocation.saveGame)
    public ResponseEntity<Void> deleteSavedGame(@RequestBody Integer gameId) {
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();
        return responseMaker.notImplemented();
    }

    // get if player is ready
    // http://localhost:8190/api/player/isReady?lobbyId=0&playerId=0


    /**
     * Method for adding a player to a lobby
     *
     * @param playerId
     * @param lobbyId
     * @auther Felix Schmidt (Felix732)
     */
    public void addPlayerToLobby(int playerId, int lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbyId == lobbies.get(i).Id) {
                lobbies.get(i).addPlayer(playerId);
            }
        }
    }

    /**
     * Method for removing a player from a lobby
     *
     * @param playerId
     * @param lobbyId
     * @auther Felix Schmidt (Felix732)
     */
    public void removePlayerToLobby(int playerId, int lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbyId == lobbies.get(i).Id) {
                lobbies.get(i).removePlayer(playerId);
            }
        }
    }

    public boolean hasLobby(int lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyId() == lobbyId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for deleting a lobby
     *
     * @param lobbyId
     * @auther Felix Schmidt (Felix732)
     */
    public void deleteLobby(int lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbyId == lobbies.get(i).Id) {
                lobbies.remove(i);
            }
        }
    }

    public boolean lobbyAlreadyExists(int lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbyId == lobbies.get(i).Id) {
                return true;
            }
        }
        return false;
    }

    public JsonObject makeJsonObject(File file) {
        JsonParser parser = new JsonParser();
        try {
            return (JsonObject) parser.parse(new FileReader(file));
        } catch (Exception e) {
            System.out.println("Error while reading file");
            return null;
        }
    }
    public void makeFileFromJsonObject (JsonObject jsonObject){
        File file = new File("src/main/resources/lobby.json");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error while writing file");
        }
    }
}
