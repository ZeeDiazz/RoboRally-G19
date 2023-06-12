package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Phase.INITIALISATION;

/**
 * @author ZeeDiazz (Zaid)
 */
public abstract class Game extends Subject implements Serializable {


    public /*final*/ Board board;
    protected Integer gameId;

    protected final List<Player> players = new ArrayList<>();
    protected Player current;

    //Represents the total amount of steps in the current game
    protected int moveCounter;

    //Represents the amount of steps in the current programming phase
    protected int step = 0;
    protected boolean stepMode;

    protected Phase phase = INITIALISATION;

    // For serializations 
    public Game(Board board, Integer gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter) {
        this.board = board;
        this.gameId = gameId;
        this.current = current;
        this.phase = phase;
        this.step = step;
        this.stepMode = stepMode;
        this.moveCounter = moveCounter;
    }

    public Game(Board board) {
        this.board = board;
    }

    public Game() {

    }

    public void addBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the games ID related to the board.
     *
     * @return The game ID
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * Gives the game board its ID, and cant be changed.
     *
     * @param gameId sets the game ID
     * @throws IllegalStateException dosnt allow the ID to change
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Gets the correct number of the player in the game
     *
     * @return number of player
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Adds a player to the game
     *
     * @param player the added player
     */
    public void addPlayer(@NotNull Player player) {
        if (/*player.board == this &&*/ !players.contains(player)) {
            players.add(player);
            if (player.getPlayerID() == 0) {
                player.setPlayerID(this.getPlayerCount());
            }
            notifyChange();
        }
    }

    /**
     * Gets the index of the player in the list of players
     *
     * @param index index of the player
     * @return the players index, else null if the index is out of bounds
     */

    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        } else {
            return null;
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

    public Player getSpecificPlayer(int playerId) {

        for (Player player : players) {
            if (player.getPlayerID() == playerId) {
                return player;
            }
        }

        return null;
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

    /**
     * Gets the current phase of the board.
     *
     * @return the current phase
     */
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
     * Gets the current step of the board
     *
     * @return the current step of the programming phase
     */
    public int getStep() {
        return step;
    }

    /**
     * Sets the current step of the board
     *
     * @param step to set the current step
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * checks if the board is in step mode
     *
     * @return ture if the board is in step mode, false if not
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Sets the step mode of the board
     *
     * @param stepMode if true, enable step mode
     *                 if false, disable step mode
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Gets the player on the boards number
     *
     * @param player for the player to get the number
     * @return the players number, or -1 if player are not on the board
     */
    public int getPlayerNumber(@NotNull Player player) {
        return players.indexOf(player);
    }

    /**
     * @return the current move counter
     */
    public int getMoveCounter() {
        return moveCounter;
    }

    /**
     * Increasing the move counter by 1
     */
    public void increaseMoveCounter() {
        this.moveCounter++;
    }

    /**
     * Returns a string of the current status of the game
     * (returns phase, player and step of the game)
     *
     * @return the current status of the game
     */
    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V2 changed the status so that it shows the phase, the player and the step
        return "Phase: " + getPhase().name() + ", Player = " + getCurrentPlayer().getName() + ", Total Steps: " + getMoveCounter();
    }

    public ArrayList<Move> resultingMoves(Move move) {
        int moveAmount = 0;
        ArrayList<Move> moves = new ArrayList<>();

        Space space = board.getSpace(move.start);
        for (int i = 0; i < move.amount; i++) {
            if (space.hasWall(move.direction)) {
                break;
            }

            space = board.getNeighbour(space, move.direction);
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

    public abstract boolean canStartGame();


    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty("gameType", this.getClass().getSimpleName());
        jsonObject.addProperty("gameId", this.gameId);
        jsonObject.addProperty("moveCounter", this.moveCounter);
        jsonObject.addProperty("playerCount", this.getPlayerCount());
        jsonObject.addProperty("step", this.step);
        jsonObject.addProperty("stepMode", this.stepMode);
        jsonObject.addProperty("phase", this.phase.toString());
        jsonObject.addProperty("currentPlayer", this.current.getName());
        jsonObject.add("board", this.board.serialize());
        JsonArray jsonArrayPlayers = new JsonArray();

        for (Player player : this.players) {
            jsonArrayPlayers.add(player.serialize());
        }
        jsonObject.add("players", jsonArrayPlayers);


        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        JsonElement gameIdJson = jsonObject.get("gameId");
        Integer gameId = (gameIdJson == null) ? null : gameIdJson.getAsInt();

        int moveCounter = jsonObject.get("moveCounter").getAsInt();
        int step = jsonObject.get("step").getAsInt();
        Phase phase = Phase.valueOf(jsonObject.get("phase").getAsString());
        boolean stepMode = jsonObject.get("stepMode").getAsBoolean();

        Board board = new Board(1, 1);
        board = (Board) board.deserialize(jsonObject.get("board"));


        int playerCount = jsonObject.get("playerCount").getAsInt();

        // Adding players
        JsonArray playersJson = jsonObject.get("players").getAsJsonArray();

        String gameType = jsonObject.getAsJsonPrimitive("gameType").getAsString();

        Player playerToAdd = (gameType.equals("OnlineGame")) ? new OnlinePlayer(null, "") : new LocalPlayer(null, "");

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            JsonObject playerJson = playersJson.get(i).getAsJsonObject();
            playerToAdd = (Player) playerToAdd.deserialize(playerJson);
            players.add(playerToAdd);
        }

        // PlayerName of current player
        String currentPlayerName = jsonObject.get("currentPlayer").getAsString();
        Player current = null;
        for (Player player : players) {
            if (currentPlayerName.equals(player.getName())) {
                current = player;
                break;
            }
        }

        Game game1;

        if (gameType.equals("OnlineGame")) {
            game1 = new OnlineGame(board, gameId, current, phase, step, stepMode, moveCounter, jsonObject.getAsJsonPrimitive("numberOfPlayersToStart").getAsInt());
        } else {
            game1 = new LocalGame(board, gameId, current, phase, step, stepMode, moveCounter);
        }
        Space robotSpace;
        Space spaceOnBoard;
        for (Player player : players) {
            player.setGame(game1);
            game1.addPlayer(player);
            robotSpace = player.robot.getSpace();
            spaceOnBoard = game1.board.getSpace(robotSpace.getPosition());
            player.robot.setSpace(spaceOnBoard);
            game1.board.getSpace(spaceOnBoard.getPosition()).setRobotOnSpace(player.robot);
        }

        return game1;
    }
}
