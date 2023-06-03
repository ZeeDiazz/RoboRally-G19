package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.CheckPointSpace;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Phase.INITIALISATION;


public class Board extends Subject {

    /**
     * Represents the total amount of steps in the current game
     */
    private int moveCounter;

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;


    private final Space[][] spaces;
    private final List<Player> players = new ArrayList<>();

    private Player current;
    private Phase phase = INITIALISATION;
    /**
     * Represents the amount of steps in the current programming phase
     */
    private int step = 0;
    private boolean stepMode;
    private int checkpointAmount;

    public Board(int moveCounter, int width, int height, String boardName, Integer gameId, Space[][] spaces, Player current, Phase phase, int step, boolean stepMode, int checkpointAmount) {
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
        this.checkpointAmount = checkpointAmount;
    }

    /**
     * Creates a new board with the given board name, width and height. Also a construtor for Board, which also creates spaces and obstacles
     *
     * @param name   the name of the board
     * @param width  the width of the board
     * @param height the height of the board
     * @author ZeeDiazz (Zaid)
     */
    public Board(int width, int height, @NotNull String name) {
        this(new Space[width][height], name);
    }

    /**
     * Creates a new board with the given default board name, width and height
     *
     * @param width  the width of the board
     * @param height the height of the board
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Board(Space[][] spaces, String name) {
        this.boardName = name;
        this.width = spaces.length;
        this.height = spaces[0].length;
        this.spaces = spaces;

        this.stepMode = false;
        this.checkpointAmount = 0;
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

    public Phase getPhase() {
        return phase;
    }

    /**
     * Sets the current phase of the board
     *
     * @param phase to set the current phase
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Gets the current player on the board
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player on the board
     *
     * @param player the current player that has been set
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }


    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        } else {
            return null;
        }
    }

    public int getPlayerCount() {
        return players.size();
    }


    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public void increaseMoveCounter() {
        this.moveCounter++;
    }

    public int getStep() {
        return step;
    }

    public int getPlayerNumber(@NotNull Player player) {
        return players.indexOf(player);
    }

    /**
     * Returns a string of the current status of the game
     * (returns phase, player and step of the game)
     *
     * @return the current status of the game
     */

    public int getMoveCounter() {
        return moveCounter;
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V2 changed the status so that it shows the phase, the player and the step
        return "Phase: " + getPhase().name() + ", Player = " + getCurrentPlayer().getName() + ", Total Steps: " + getMoveCounter();
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void addCheckpoint(Position position) {
        this.spaces[position.X][position.Y] = new CheckPointSpace(position, checkpointAmount++);
    }


    /**
     * Rotate a whole board to the left.
     *
     * @param board the board to rotate.
     * @return a rotated copy of the given board.
     * @author Daniel Jensen
     */
    public static Board rotateLeft(Board board) {
        Space[][] newSpaces = new Space[board.height][board.width];
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Position newPosition = new Position(y, board.width - x - 1);
                Space newSpace = board.spaces[x][y].copy(newPosition);
                newSpace.rotateLeft();
                newSpaces[newPosition.X][newPosition.Y] = newSpace;
            }
        }

        return new Board(newSpaces, board.boardName);
    }

    /**
     * Rotate a whole board to the right.
     *
     * @param board the board to rotate.
     * @return a rotated copy of the given board.
     * @author Daniel Jensen
     */
    public static Board rotateRight(Board board) {
        return rotateLeft(rotateLeft(rotateLeft(board)));
    }

    /**
     * Add to boards together, and get the resulting board.
     * This is useful because the original game has different physical boards, which you can combine to create one large board.
     *
     * @param board   the base board, which the other board will be added to.
     * @param adding  the board we're adding.
     * @param offset  the new position of "adding"s (0, 0).
     * @param newName the name of the new unified board.
     * @return a board with all the spaces of the two boards, with the correct positions according to the offset.
     * @author Daniel Jensen
     */
    public static Board add(Board board, Board adding, Position offset, String newName) {
        Position currentTopLeft = board.spaces[0][0].position;
        Position currentBottomRight = board.spaces[board.width - 1][board.height - 1].position;
        Position addingTopLeft = Position.add(adding.spaces[0][0].position, offset);
        Position addingBottomRight = Position.add(adding.spaces[adding.width - 1][adding.height - 1].position, offset);

        int newWidth = Math.max(Math.abs(currentTopLeft.X - addingBottomRight.X), Math.abs(addingTopLeft.X - currentBottomRight.X)) + 1;
        int newHeight = Math.max(Math.abs(currentTopLeft.Y - addingBottomRight.Y), Math.abs(addingTopLeft.Y - currentBottomRight.Y)) + 1;

        Space[][] newSpaces = new Space[newWidth][newHeight];
        // Add all the "board" spaces
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                newSpaces[x][y] = board.spaces[x][y].copy(new Position(x, y));
            }
        }
        // Add all the "adding" spaces
        for (int x = 0; x < adding.width; x++) {
            for (int y = 0; y < adding.height; y++) {
                newSpaces[x + offset.X][y + offset.Y] = adding.spaces[x][y].copy(new Position(x + offset.X, y + offset.Y));
            }
        }

        return new Board(newSpaces, newName);
    }

    /**
     * Add to boards together, and get the resulting board. The resulting board will have the name of "board".
     * This is useful because the original game has different physical boards, which you can combine to create one large board.
     *
     * @param board  the base board, which the other board will be added to.
     * @param adding the board we're adding.
     * @param offset the new position of "adding"s (0, 0).
     * @return a board with all the spaces of the two boards, with the correct positions according to the offset.
     * @author Daniel Jensen
     */
    public static Board add(Board board, Board adding, Position offset) {
        return add(board, adding, offset, board.boardName);
    }

    /**
     * Adds a player to the game
     *
     * @param player the added player
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }


}

