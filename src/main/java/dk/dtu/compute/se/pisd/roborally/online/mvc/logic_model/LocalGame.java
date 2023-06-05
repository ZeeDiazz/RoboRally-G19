package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;

public class LocalGame extends Game {
    
    // For serialization
    public LocalGame(Board board, Integer gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
    }

    public LocalGame(Board board) {
        super(board);
    }

    @Override
    public boolean canStartGame() {
        return true;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        return null;
    }
}
