package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.Client;

public class OnlineGame extends Game {
    private Client client;
    private int numberOfPlayersToStart;

    /**
     * Constructor used in serializations
     *
     * @author Zigalow
     */
    public OnlineGame(Board board, int gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter, int numberOfPlayersToStart) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    /**
     * Primary constructor for creating an OnlineGame
     *
     * @param board
     */
    public OnlineGame(Board board, int numberOfPlayersToStart) {
        super(board);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    public int getNumberOfPlayersToStart() {
        return numberOfPlayersToStart;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();

        jsonObject.addProperty("numberOfPlayersToStart", getNumberOfPlayersToStart());
        return jsonObject;
    }
}
