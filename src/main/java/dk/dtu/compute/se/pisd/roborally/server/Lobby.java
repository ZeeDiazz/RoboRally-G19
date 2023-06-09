package dk.dtu.compute.se.pisd.roborally.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby {
    private final int id;
    private int readyCount = 0;
    private final Map<Integer, Boolean> playerStatus;

    /**
     * Constructor for Lobby
     * @param id, an integer value to identify the lobby
     * @author Felix Schmidt (Felix732)
     */
    public Lobby (int id){
        this.id = id;
        this.playerStatus = new HashMap<>();
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

    /**
     * Method to set a player ready
     * @param playerId
     * @author Felix Schmidt (Felix732)
     */
    public void setIsReady(int playerId) {
        if (!hasPlayer(playerId)) {
            return;
        }
        if (!playerIsReady(playerId)) {
            playerStatus.put(playerId, true);
            readyCount++;
        }
    }

    /**
     * Method to set a player not ready
     * @param playerId
     * @author Felix Schmidt (Felix732)
     */
    public void setNotReady (int playerId) {
        if (!hasPlayer(playerId)) {
            return;
        }
        if (playerIsReady(playerId)) {
            playerStatus.put(playerId, true);
            readyCount--;
        }
    }

    public boolean isReady() {
        return readyCount == playerStatus.size();
    }

    public void resetReadyStatus() {
        for (int playerId : getPlayerIds()) {
            setNotReady(playerId);
        }
    }
}
