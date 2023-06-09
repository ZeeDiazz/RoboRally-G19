package dk.dtu.compute.se.pisd.roborally.server;

import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.List;

public class Lobby {
    int Id;
    int readyCount = 0;
    List<Integer> players;
    Boolean[] isReady;

    /**
     * Constructor for Lobby
     * @param Id, an integer value to identify the lobby
     * @auther Felix Schmidt (Felix732)
     */
    public Lobby (int Id){
        this.Id = Id;
        this.players = new ArrayList<>();
        isReady = new Boolean[6];
        for (int i = 0; i < isReady.length; i++) {
            isReady[i] = false;
        }
    }

    /**
     * Method to get the number of players ready in a lobby
     * @return
     * @auther Felix Schmidt (Felix732)
     */
    public int getPlayersReadyCount() {
        for (int i = 0; i < isReady.length; i++) {
            if (isReady[i]) {
                readyCount++;
            }
        }
        return readyCount;
    }

    /**
     * Method to add a player to a lobby utilizing the List interface method add
     * @param playerId
     * @auther Felix Schmidt (Felix732)
     */
    public void addPlayer(int playerId){
        players.add(playerId);
    }
    /**
     * Method to get the lobby id
     * @return id, integer value lobby id
     * @auther Felix Schmidt (Felix732)
     */
    public int getLobbyId(){
        return this.Id;
    }

    /**
     * Method to get the list of players in a lobby
     * @return players, a list of players
     * @auther Felix Schmidt (Felix732)
     */
    public List<Integer> getPlayers(){
        return players;
    }

    /**
     * Method to remove a player from a lobby
     * @param playerId
     * @auther Felix Schmidt (Felix732)
     */
    public void removePlayer(int playerId){
        players.remove(playerId);
    }

    /**
     * Method to set a player ready
     * @param playerId
     * @auther Felix Schmidt (Felix732)
     */
    public void setIsReady(int playerId){
        for (int i = 0; i < this.players.size(); i++) {
            if (playerId == this.players.get(i)) {
                this.isReady[i] = true; //hey
            }
        }
    }

    /**
     * Method to set a player not ready
     * @param playerId
     * @auther Felix Schmidt (Felix732)
     */
    public void setNotReady (int playerId){
        for (int i = 0; i < players.size(); i++){
            if (playerId == players.get(i)){
                isReady[i] = false;
            }
        }
    }
    public Boolean[] getIsReady(){
        return isReady;
    }




}
