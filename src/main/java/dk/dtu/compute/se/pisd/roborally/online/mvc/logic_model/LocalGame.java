package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

public class LocalGame extends Game {
    /**
     * Constructor used in serializations
     *
     * @author Zigalow
     */
    public LocalGame(Board board, Integer gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
    }

    /**
     * Primary constructor for creating a localGame
     *
     * @param board
     */

    public LocalGame(Board board) {
        super(board);
    }


}
