package dk.dtu.compute.se.pisd.roborally.server;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import dk.dtu.compute.se.pisd.roborally.restful.ResponseMaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @auther Felix Schmidt (Felix723)
 */

@RestController
public class Server {
    private ResponseMessage responseMessages;
    public Server() {

    }
    // intialize list of lobbies, not null
    List<Lobby> lobbies = new ArrayList<>();
    private int lobbySize = 0;
    private final AtomicLong counter = new AtomicLong();

    public boolean hasLobby(int lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyId() == lobbyId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for handling get requests to /greeting
     * @param name a string value
     * @return a Greeting object using the counter and template
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return (new ResponseMaker<String>()).ok();
    }

    @GetMapping(ResourceLocation.allGames)
    public ResponseEntity<Integer[]> getAllGames() {
        ResponseMaker<Integer[]> responseMaker = new ResponseMaker<>();

        Integer[] ids = new Integer[lobbies.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = lobbies.get(i).getLobbyId();
        }

        return responseMaker.itemResponse(ids);
    }

    @GetMapping(ResourceLocation.specificGame)
    public ResponseEntity<Integer> getGameInfo(@RequestParam Integer lobbyId) {
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();

        boolean lobbyExists = hasLobby(lobbyId);
        if (lobbyExists) {
            return responseMaker.itemResponse(lobbyId);
        }
        return responseMaker.notFound();
    }

    /**
     * Method for handling post requests to /api/lobby/create
     * @param lobbyId Integer value to identify the lobby
     * @return a Greeting object using the counter and lobbyCreated template
     * @author Felix Schmidt (Felix732) & Daniel Jensen
     */
    @PostMapping(ResourceLocation.specificGame)
    public ResponseEntity<Integer> lobbyCreateRequest(@RequestBody(required = false) Integer lobbyId) {
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();
        Random rng = new Random();

        System.out.println("Requested lobby id: " + lobbyId);
        // Make a random id that we don't already use
        while (lobbyId == null || hasLobby(lobbyId)) {
            lobbyId = rng.nextInt(0, Integer.MAX_VALUE);
        }
        lobbies.add(new Lobby(lobbyId));

        return responseMaker.created(lobbyId);
        /*
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyCreatedMessage(lobbyId));
         */
    }

    @DeleteMapping(ResourceLocation.specificGame)
    public ResponseEntity<Void> deleteActiveGame(@RequestBody JsonObject info) {
        ResponseMaker<Void> responseMaker = new ResponseMaker<>();

        if (!info.has("lobbyId")) {
            return responseMaker.methodNotAllowed();
        }
        else if (!info.has("playerId")) {
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

        if (!playerInLobby || lobby.getPlayers().get(0) != playerId) {
            return responseMaker.forbidden();
        }
        return responseMaker.ok();
    }

    /**
     * Method for handling post request to /api/lobby/join
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
        }
        else if (!info.has("playerId")) {
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
        }
        else if (!info.has("playerId")) {
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
        }
        else {
            lobby.setNotReady(playerId);
        }
        return responseMaker.ok();
    }

    @GetMapping(ResourceLocation.saveGame)
    public ResponseEntity<Integer> loadGame(@RequestParam Integer gameId) {
        ResponseMaker<Integer> responseMaker = new ResponseMaker<>();
        return responseMaker.notImplemented();
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



















    /**
     * Method for handling post request to /api/player/ready
     * @param lobbyId Integer value to identify the lobby
     * @param playerId Integer value to identify the player
     * @return  a Greeting object using the counter and playerReady template
     */
    @GetMapping(value = "/api/player/ready")
    public Greeting playerSetReadyRequest(@RequestParam(value = "lobbyId") Integer lobbyId,
                                          @RequestParam(value = "playerId") Integer playerId) {
        lobbies.get(lobbyId).setIsReady(playerId);
        return new Greeting(counter.incrementAndGet(), responseMessages.getPlayerReadyMessage(playerId, lobbyId));
    }

    /**
     * Method for handling post request to /api/player/unready
     * @param lobbyId Integer value to identify the lobby
     * @param playerId Integer value to identify the player
     * @return a Greeting object using the counter and playerNotReady template
     * @auther Felix Schmidt (Felix732)
     */
    @PutMapping(value = "/api/player/unready")
    public Greeting playerNotreadyRequest(@RequestParam(value = "lobbyId") Integer lobbyId,
                                          @RequestParam(value = "playerId") Integer playerId){
        lobbies.get(lobbyId).setNotReady(playerId);
        return new Greeting(counter.incrementAndGet(), responseMessages.getPlayerNotReadyMessage(playerId, lobbyId));
    }

    /**
     * Method for handling post request to /api/player/leave
     * @param lobbyId Integer value to identify the lobby
     * @param playerId Integer value to identify the player
     * @return a Greeting object using the counter and lobbyLeaved template
     */
    @PutMapping(value = "/api/player/leave")
    public Greeting playerLeaveRequest(@RequestParam(value = "lobbyId")Integer lobbyId,
                                       @RequestParam(value = "playerId") Integer playerId ) {
        removePlayerToLobby(playerId, lobbyId);
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyLeavedMessage(playerId,lobbyId));
    }

    /**
     * Method for handling post request to /api/lobby/delete
     * @param lobbyId Integer value to identify the lobby
     * @return a Greeting object using the counter and lobbyDeleted template
     * @auther Felix Schmidt (Felix732)
     */
    @DeleteMapping(value = "/api/lobby/delete")
    public Greeting lobbyDeleteRequest(@RequestParam(value = "lobbyId")Integer lobbyId) {
        deleteLobby(lobbyId);
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyDeletedMessage(lobbyId));
    }

    /* -- -- -- */
    // simple get request to check if server is running
    // http://localhost:8190/api/server/running

    /**
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping(value = "/api/server/running")
    public Greeting serverRunningRequest() {
        return new Greeting(counter.incrementAndGet(), "Server is running!");
    }
    // get if lobby is ready
    // http://localhost:8190/api/lobby/ready?lobbyId=0
    /**
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping(value = "/api/lobby/ready")
    public Greeting lobbyReadyRequest(@RequestParam(value = "lobbyId")Integer lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).Id == lobbyId) {
                if (lobbies.get(i).getPlayersReadyCount() >= 2) {
                    return new Greeting(counter.incrementAndGet(), "Lobby is ready!");
                }
            }
        }
        return new Greeting(counter.incrementAndGet(), "Lobby is not ready!");
    }
    // get if player is ready
    // http://localhost:8190/api/player/isReady?lobbyId=0&playerId=0
    /**
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping(value = "/api/player/isReady")
    public Greeting isPlayerReadyRequest(@RequestParam(value = "lobbyId")Integer lobbyId,
                                         @RequestParam(value = "playerId") Integer playerId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).Id == lobbyId) {
                if (lobbies.get(i).getIsReady()[playerId]) {
                    return new Greeting(counter.incrementAndGet(), "Player is ready!");
                }
            }
        }
        return new Greeting(counter.incrementAndGet(), "Player is not ready!");
    }
    // get size of lobby
    // http://localhost:8190/api/lobby/size?lobbyId=2
    /**
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping(value = "/api/lobby/size")
    public Greeting lobbySizeRequest(@RequestParam(value = "lobbyId") Integer lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).Id == lobbyId) {
                lobbySize = lobbies.get(i).getPlayers().size();
                return new Greeting(counter.incrementAndGet(), "Lobby size is: " + lobbySize);
            }
        }
        return new Greeting(counter.incrementAndGet(), "Lobby does not exist!");
    }

    /**
     * Method for adding a player to a lobby
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
     * @param playerId
     * @param lobbyId
     * @auther Felix Schmidt (Felix732)
     */
    public void removePlayerToLobby(int playerId,int lobbyId) {
        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbyId == lobbies.get(i).Id) {
                lobbies.get(i).removePlayer(playerId);
            }
        }
    }

    /**
     * Method for deleting a lobby
     * @param lobbyId
     * @auther Felix Schmidt (Felix732)
     */
    public void deleteLobby(int lobbyId){
        for (int i = 0; i < lobbies.size(); i++){
            if (lobbyId == lobbies.get(i).Id) {
                lobbies.remove(i);
            }
        }
    }
}
