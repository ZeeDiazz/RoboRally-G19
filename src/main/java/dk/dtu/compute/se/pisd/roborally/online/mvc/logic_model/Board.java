package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.CheckPointSpace;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import org.jetbrains.annotations.NotNull;


public class Board extends Subject implements Serializable {

    /**
     * Represents the total amount of steps in the current game
     */
    //private int moveCounter;

    public final int width;

    public final int height;

    public final String boardName;

    //private Integer gameId;


    private final Space[][] spaces;
    //-------------DELETE FROM HERE
    /*
    private final List<Player> players = new ArrayList<>();

    private Player current;
    private Phase phase = INITIALISATION;
    /**
     * Represents the amount of steps in the current programming phase

    private int step = 0;
    private boolean stepMode;
*/
    //------------TO HERE
    private int checkpointAmount;

    /**
     * A constructor for internal use, which has literally all the parts of the Board given. Used for deserialization
     *
     * @param width
     * @param height
     * @param boardName
     * @param spaces
     * @param checkpointAmount
     * @author Daniel Jensen
     */
    private Board(int width, int height, String boardName, Space[][] spaces, int checkpointAmount) {
        this.width = width;
        this.height = height;
        this.boardName = boardName;
        this.spaces = spaces;
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

        //this.stepMode = false;
        this.checkpointAmount = 0;
    }

    /**
     * Gets the given space on the board position.
     *
     * @param position
     * @return
     * @author Daniel
     */
    public Space getSpace(Position position) {
        return getSpace(position.X, position.Y);
    }

    /**
     * Gets the given coordinates space on the board.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the given coordinates, or null if out of bounds
     * @author ZeeDiazz (Zaid)
     */
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
                // Todo - Does the parameters get passed correctly?
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

    //-------------------------- Delete this and move to Game
    /*
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
     * @param phase to set the current phase

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Gets the current player on the board
     * @return the current player

    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player on the board
     * @param player the current player that has been set

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
    }*/
//-------------------------- To here


    public int getCheckpointAmount() {
        return checkpointAmount;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("width", this.width);
        jsonObject.addProperty("height", this.height);
        jsonObject.addProperty("boardName", this.boardName);
        jsonObject.addProperty("checkpointAmount", this.checkpointAmount);

        JsonArray jsonArraySpaces = new JsonArray();
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Space currentSpace = this.spaces[i][j];
                jsonArraySpaces.add(currentSpace.serialize());
            }
        }
        jsonObject.add("spaces", jsonArraySpaces);


        return jsonObject;

    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        int width = jsonObject.get("width").getAsInt();
        int height = jsonObject.get("height").getAsInt();
        String boardName = jsonObject.get("boardName").getAsString();
        int checkPointAmount = jsonObject.get("checkpointAmount").getAsInt();


        Space[][] spaces = new Space[width][height];

        JsonArray spacesJson = jsonObject.get("spaces").getAsJsonArray();
        Space space = new Space(new Position(0, 0));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                JsonObject spaceJson = spacesJson.get(x * height + y).getAsJsonObject();
                spaces[x][y] = (Space) space.deserialize(spaceJson);
            }
        }

        return new Board(width, height, boardName, spaces, checkPointAmount);


    }
}