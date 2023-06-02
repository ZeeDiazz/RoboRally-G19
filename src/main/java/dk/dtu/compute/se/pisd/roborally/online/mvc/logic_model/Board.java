package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;

import static dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Phase.INITIALISATION;


public class Board {

    private int moveCounter;

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;


    private final Space[][] spaces;

    private Player current;
    private Phase phase = INITIALISATION;
    /**
     * Represents the amount of steps in the current programming phase
     */
    private int step = 0;
    private boolean stepMode;
    private int checkpointCount;

    private Board(int moveCounter, int width, int height, String boardName, Integer gameId, Space[][] spaces, Player current, Phase phase, int step, boolean stepMode, int checkpointCount) {
        this.moveCounter = moveCounter;
        this.width = width;
        this.height = height;
        this.boardName = boardName;
        this.gameId = gameId;
        this.spaces = spaces;
        this.current = current;
        this.phase = phase;
        this.step = step;
        this.stepMode = stepMode;
        this.checkpointCount = checkpointCount;
    }

    public Space getSpace(Position position) {
        return getSpace(position.X, position.Y);
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public Space getNeighbour(@NotNull Space space, @NotNull HeadingDirection headingDirection) {
        return getSpace(Position.move(space.position, headingDirection));
    }

    public ArrayList<Move> resultingMoves(Move move) {
        int moveAmount = 0;
        ArrayList<Move> moves = new ArrayList<>();

        Space space = getSpace(move.start);
        for (int i = 0; i < move.amount; i++) {
            if (space.hasWall(move.direction)) {
                break;
            }

            space = getNeighbour(space, move.direction);
            if (space == null) {
                moveAmount++;
                break;
            } else if (space.hasWall(HeadingDirection.oppositeHeadingDirection(move.direction))) {
                break;
            } else if (space.getRobot() != null) {
                // Can maximally move the full amount, minus the part already moved
                int moveOtherAmount = move.amount - moveAmount;
                Move otherPlayerMove = new Move(space.position, move.direction, moveOtherAmount, space.getRobot());

                int leftToMove = 0;
                for (Move resultingMove : resultingMoves(otherPlayerMove)) {
                    moves.add(resultingMove);
                    leftToMove = Math.max(leftToMove, resultingMove.amount);
                }
                moveAmount += leftToMove;

                break;
            }
            moveAmount++;
        }

        moves.add(new Move(move.start, move.direction, moveAmount, move.moving));
        return moves;
    }
}

