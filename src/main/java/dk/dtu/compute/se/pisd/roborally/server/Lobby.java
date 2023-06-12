package dk.dtu.compute.se.pisd.roborally.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class Lobby {
    private static final Random rng = new Random();

    private final int id;
    private int readyCount = 0;
    private int minimumPlayers = 0;
    private final Map<Integer, Boolean> playerStatus;
    private boolean active;
    private String boardName;

    /**
     * Constructor for Lobby
     * @param id, an integer value to identify the lobby
     * @author Felix Schmidt (Felix732)
     */
    public Lobby(int id, String boardName) {
        this.id = id;
        this.playerStatus = new HashMap<>();
        this.boardName = boardName;
    }

    /**
     * Method to get the lobby id
     * @return id, integer value lobby id
     * @author Felix Schmidt (Felix732)
     */
    public int getId(){
        return this.id;
    }

    /**
     * Method to add a player to a lobby utilizing the List interface method add
     * @param playerId
     * @author Felix Schmidt (Felix732)
     */
    public void addPlayer(int playerId){
        playerStatus.put(playerId, false);
    }

    /**
     * Method to remove a player from a lobby
     * @param playerId
     * @author Felix Schmidt (Felix732)
     */
    public void removePlayer(int playerId){
        if (!hasPlayer(playerId)) {
            return;
        }
        playerStatus.remove(playerId);
    }

    public boolean hasPlayer(int playerId) {
        return playerStatus.containsKey(playerId);
    }

    public boolean isHost(int playerId) { return getPlayerIds().get(0) == playerId; }

    /**
     * Method to get the number of players ready in a lobby
     * @return
     * @author Felix Schmidt (Felix732)
     */
    public int getPlayersReadyCount() {
        return readyCount;
    }

    /**
     * Method to get the list of players in a lobby
     * @return players, a list of players
     * @author Felix Schmidt (Felix732)
     */
    public List<Integer> getPlayerIds(){
        return playerStatus.keySet().stream().toList();
    }

    private boolean playerIsReady(int playerId) {
        return hasPlayer(playerId) && playerStatus.get(playerId);
    }

    public void setReadyStatus(int playerId, boolean newStatus) {
        if (!hasPlayer(playerId)) {
            return;
        }
        boolean currentStatus = playerIsReady(playerId);
        if (currentStatus != newStatus) {
            playerStatus.put(playerId, newStatus);
            readyCount += newStatus ? 1 : -1;
        }
    }

    public boolean isReady(){
        return readyCount == playerStatus.size();
    }
    public boolean canLaunch(){
        return playerStatus.size() >= minimumPlayers;
    }

    public void resetReadyStatus() {
        for (int playerId : getPlayerIds()) {
            setReadyStatus(playerId, false);
        }
    }
    public int getMinimumPlayers() {
        return minimumPlayers;
    }
    public void setMinimumPlayers(int minimumPlayers) {
        this.minimumPlayers = minimumPlayers;
    }
    public int getNumberOfPlayers() {
        return playerStatus.size();
    }
    public void setActive() {
        this.active = true;
    }
    public boolean isActive() {
        return this.active;
    }
    public String getBoardName() {
        return this.boardName;
    }

    public int makePlayerId() {
        int id = -1;
        boolean takenId = true;
        while (takenId) {
            id = rng.nextInt(0, Integer.MAX_VALUE);
            takenId = hasPlayer(id);
        }
        return id;
    }
}
