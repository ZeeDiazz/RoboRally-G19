package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class OnlineGame extends Game {


    private int numberOfPlayersToStart;


    public OnlineGame(Board board, Integer gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter, int numberOfPlayersToStart) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    @Override
    public boolean canStartGame() {
        return this.players.size() == numberOfPlayersToStart;
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
