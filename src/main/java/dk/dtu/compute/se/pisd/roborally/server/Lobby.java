package dk.dtu.compute.se.pisd.roborally.server;

import java.util.*;

public class Lobby {
    private static final Random rng = new Random();

    private final int id;
    private int stepsTaken = 0;
    private int minimumPlayers;
    private final List<Integer> playerIds;
    private final List<Boolean> readyStatus;
    private final List<Boolean> hasRetrievedInfo;
    private final List<String> latestMoves;
    private final List<String> interactions;
    private boolean active;
    private String boardName;

    /**
     * Constructor for Lobby
     * @param id, an integer value to identify the lobby
     * @author Felix Schmidt (Felix732)
     */
    public Lobby(int id, int minimumPlayers, String boardName) {
        this.id = id;
        this.minimumPlayers = minimumPlayers;
        this.playerIds = new ArrayList<>();
        this.readyStatus = new ArrayList<>();
        this.hasRetrievedInfo = new ArrayList<>();
        this.latestMoves = new ArrayList<>();
        this.interactions = new ArrayList<>();
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

    private int getPlayerIndex(int playerId) {
        for (int i = 0; i < playerIds.size(); i++) {
            if (playerIds.get(i) == playerId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method to add a player to a lobby utilizing the List interface method add
     * @param playerId
     * @author Felix Schmidt (Felix732)
     */
    public void addPlayer(int playerId) {
        playerIds.add(playerId);
        readyStatus.add(false);
        hasRetrievedInfo.add(false);
        latestMoves.add("");
    }

    /**
     * Method to remove a player from a lobby
     * @param playerId
     * @author Felix Schmidt (Felix732)
     */
    public void removePlayer(int playerId) {
        int playerIndex = getPlayerIndex(playerId);
        if (playerIndex == -1) {
            return;
        }
        playerIds.remove(playerIndex);
        readyStatus.remove(playerIndex);
        hasRetrievedInfo.remove(playerIndex);
        latestMoves.remove(playerIndex);
    }

    public boolean hasPlayer(int playerId) {
        return getPlayerIndex(playerId) != -1;
    }

    public boolean isHost(int playerId) { return getPlayerIds().get(0) == playerId; }

    /**
     * Method to get the number of players ready in a lobby
     * @return
     * @author Felix Schmidt (Felix732)
     */
    public int getReadyCount() {
        int readyCount = 0;
        for (int i = 0; i < getPlayerCount(); i++) {
            if (readyStatus.get(i)) {
                readyCount++;
            }
        }
        return readyCount;
    }

    /**
     * Method to get the list of players in a lobby
     * @return players, a list of players
     * @author Felix Schmidt (Felix732)
     */
    public List<Integer> getPlayerIds() {
        // streaming to a list, to make a copy, instead of giving the internal copy away
        return playerIds.stream().toList();
    }

    private boolean playerIsReady(int playerId) {
        int playerIndex = getPlayerIndex(playerId);
        if (playerIndex == -1) {
            return false;
        }
        return readyStatus.get(playerIndex);
    }

    public void setReadyStatus(int playerId, boolean newStatus) {
        if (!hasPlayer(playerId)) {
            return;
        }
        readyStatus.set(getPlayerIndex(playerId), newStatus);
        System.out.println("Ready status: " + Arrays.toString(readyStatus.toArray()));
    }

    public boolean hasRetrievedInfo(int playerId) {
        int playerIndex = getPlayerIndex(playerId);
        if (playerIndex == -1) {
            return false;
        }
        return hasRetrievedInfo.set(playerIndex, true);
    }

    public boolean isReady() {
        return getReadyCount() == playerIds.size();
    }
    public boolean allHaveInfo() {
        for (boolean hasRetrieved : hasRetrievedInfo) {
            if (hasRetrieved) {
                continue;
            }
            return false;
        }
        return true;
    }
    public boolean canLaunch(){
        return getPlayerCount() >= minimumPlayers;
    }

    public void resetReadyStatus() {
        for (int playerId : getPlayerIds()) {
            setReadyStatus(playerId, false);
            hasRetrievedInfo.set(getPlayerIndex(playerId), false);
        }
        stepsTaken++;
    }
    public int getMinimumPlayers() {
        return minimumPlayers;
    }
    public int getPlayerCount() {
        return playerIds.size();
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

    public void updateMoves(int playerId, String moves) {
        int index = getPlayerIndex(playerId);
        if (index == -1) {
            return;
        }
        latestMoves.set(index, moves);
    }
    public void resetMoves() {
        for (int i = 0; i < getPlayerCount(); i++) {
            latestMoves.set(i, "");
        }
    }
    public void resetInteractions() {
        interactions.clear();
    }
    public List<String> getLatestMoves() {
        // streaming to a list, to make a copy, instead of giving the internal copy away
        return latestMoves.stream().toList();
    }
    public List<String> getInteractions() {
        // streaming to a list, to make a copy, instead of giving the internal copy away
        return interactions.stream().toList();
    }
    public void addInteraction(String interaction) {
        this.interactions.add(interaction);
    }

    public int getStepsTaken() {
        return stepsTaken;
    }
}
