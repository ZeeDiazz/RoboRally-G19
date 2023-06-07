package dk.dtu.compute.se.pisd.roborally.server;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @auther Felix Schmidt (Felix723)
 */

@RestController
public class Server {
    private ResponseMessage responseMessages;
    public Server(ResponseMessage responseMessages) {
        this.responseMessages = responseMessages;
    }
    // intialize list of lobbies, not null
    List<Lobby> lobbies = new ArrayList<>();
    private int lobbySize = 0;
    private final AtomicLong counter = new AtomicLong();

    /**
     * Method for handling get requests to /greeting
     * @param name a string value
     * @return a Greeting object using the counter and template
     * @auther Felix Schmidt (Felix732)
     */
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), responseMessages.greetingMessage(name));
    }

    /**
     * Method for handling post requests to /api/lobby/create
     * @param lobbyId Integer value to identify the lobby
     * @return a Greeting object using the counter and lobbyCreated template
     * @auther Felix Schmidt (Felix732)
     */
    @PostMapping(value = "/api/lobby/create")
    public Greeting lobbyCreateRequest(@RequestParam(value = "lobbyId") Integer lobbyId) {

        lobbies.add(new Lobby(lobbyId));
        return new Greeting(counter.incrementAndGet(), responseMessages.getLobbyCreatedMessage(lobbyId));
    }

    /**
     * Method for handling post request to /api/lobby/join
     * @param lobbyId Integer value to identify the lobby
     * @param playerId Integer value to identify the player
     * @return a Greeting object using the counter and lobbyJoined template
     * @auther Felix Schmidt (Felix732)
     */
    @PostMapping(value = "/api/player/join")
    public Greeting playerJoinRequest(@RequestParam(value = "lobbyId") Integer lobbyId,
                                      @RequestParam(value = "playerId") Integer playerId) {
        // get the lobby with matching id
        addPlayerToLobby(playerId, lobbyId);
        return new Greeting(counter.incrementAndGet(),responseMessages.getLobbyJoinedMessage(playerId,lobbyId));
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
