package dk.dtu.compute.se.pisd.roborally.server;
import dk.dtu.compute.se.pisd.roborally.restful.ResponseMaker;
import org.springframework.web.bind.annotation.*;

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
    private ResponseMaker responseMaker;
    public Server(ResponseMaker responseMaker, ResponseMessage responseMessages) {
        this.responseMaker = responseMaker;
        this.responseMessages = responseMessages;
    }
    // intialize list of lobbies, not null
    List<Lobby> lobbies = new ArrayList<>();
    private int lobbySize = 0;
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Message greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Message(responseMaker.ok(),responseMessages.greetingMessage(name));
    }
    /* -- resource /game -- */

    @PostMapping(value = "/game")
    public Message lobbyCreateRequest(@RequestParam(value = "lobbyId") Integer lobbyId) {
        if(lobbyAlreadyExists(lobbyId)) {
            return new Message(responseMaker.forbidden(),responseMessages.getLobbyAlreadyExistsMessage(lobbyId));
        }
        lobbies.add(new Lobby(lobbyId));
        return new Message(responseMaker.accepted(),responseMessages.getLobbyCreatedMessage(lobbyId));
    }
    @DeleteMapping(value = "/game")
    public Message lobbyDeleteRequest(@RequestParam(value = "lobbyId")Integer lobbyId) {
        deleteLobby(lobbyId);
        return new Message(responseMaker.accepted(),responseMessages.getLobbyDeletedMessage(lobbyId));
    }
    /* -- resource /game/join -- */
    @PostMapping(value = "/game/join")
    public Message playerJoinRequest(@RequestParam(value = "lobbyId") Integer lobbyId,
                                     @RequestParam(value = "playerId") Integer playerId) {
        // get the lobby with matching id
        addPlayerToLobby(playerId, lobbyId);
        return new Message(responseMaker.accepted(),responseMessages.getLobbyJoinedMessage(lobbyId,playerId));
    }
    /* -- resource /game/status -- */
    // get if lobby is ready
    // http://localhost:8190/api/lobby/ready?lobbyId=0
    @GetMapping(value = "/game/status")
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
    @GetMapping(value = "game/statustwo")
    public Message playerSetReadyRequest(@RequestParam(value = "lobbyId") Integer lobbyId,
                                         @RequestParam(value = "playerId") Integer playerId) {
        lobbies.get(lobbyId).setIsReady(playerId);
        return new Message(responseMaker.accepted(),responseMessages.getPlayerReadyMessage(playerId,lobbyId));
    }
    @PutMapping(value = "/api/player/unready")
    public Message playerNotreadyRequest(@RequestParam(value = "lobbyId") Integer lobbyId,
                                         @RequestParam(value = "playerId") Integer playerId){
        lobbies.get(lobbyId).setNotReady(playerId);
        return new Message(responseMaker.accepted(),responseMessages.getPlayerNotReadyMessage(playerId,lobbyId));
    }
    // get size of lobby
    // http://localhost:8190/api/lobby/size?lobbyId=2
    // TODO: move into game/status
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
    /* -- resource /game/save -- */
    // TODO

    /* -- resource /player -- */
    @DeleteMapping(value = "player")
    public Message playerLeaveRequest(@RequestParam(value = "lobbyId")Integer lobbyId,
                                      @RequestParam(value = "playerId") Integer playerId ) {
        // make it so you can only delete yourself
        removePlayerToLobby(playerId, lobbyId);
        return new Message(responseMaker.accepted(),responseMessages.getLobbyLeavedMessage(playerId,lobbyId));
    }
    @GetMapping(value = "/player")
    public Message playerInfo(@RequestParam(value = "lobbyId")Integer lobbyId,
                              @RequestParam(value = "playerId") Integer playerId){
        // TODO
        return new Message(responseMaker.accepted(),responseMessages.getLobbyDeletedMessage(lobbyId));
    }
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

    /* -- -- -- */
    // simple get request to check if server is running
    // http://localhost:8190/api/server/running

    @GetMapping(value = "/api/server/running")
    public Greeting serverRunningRequest() {
        return new Greeting(counter.incrementAndGet(), "Server is running!");
    }

    // get if player is ready
    // http://localhost:8190/api/player/isReady?lobbyId=0&playerId=0


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
    public boolean lobbyAlreadyExists(int lobbyId){
        for (int i = 0; i < lobbies.size(); i++){
            if (lobbyId == lobbies.get(i).Id) {
                return true;
            }
        }
        return false;
    }
}
