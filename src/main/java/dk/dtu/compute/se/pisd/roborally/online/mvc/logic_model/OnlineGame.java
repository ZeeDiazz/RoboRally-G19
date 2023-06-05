package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;

public class OnlineGame extends Game {


    private int numberOfPlayersToStart;


    public OnlineGame(Board board, Integer gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
    }

    @Override
    public boolean canStartGame() {
        return this.players.size() == numberOfPlayersToStart;
    }

    public OnlineGame(Board board, int numberOfPlayersToStart) {
        super(board);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();

        jsonObject.addProperty("numberOfPlayersToStart", this.numberOfPlayersToStart);

        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        return null;
    }
}
