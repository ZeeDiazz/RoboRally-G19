package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.Client;

public class OnlineGame extends Game {
    private Client client;
    private int numberOfPlayersToStart;


    public OnlineGame(Board board, int gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter, int numberOfPlayersToStart) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }
    
    public OnlineGame(Board board, int gameId, int numberOfPlayersToStart, Client client) {
        this.board = board;
        this.gameId = gameId;
        this.numberOfPlayersToStart = numberOfPlayersToStart;
        this.current = client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public OnlineGame(Board board, int numberOfPlayersToStart) {
        super(board);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    public int getNumberOfPlayersToStart() {
        return numberOfPlayersToStart;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();

        jsonObject.addProperty("numberOfPlayersToStart", getNumberOfPlayersToStart());
        return jsonObject;
    }
}
