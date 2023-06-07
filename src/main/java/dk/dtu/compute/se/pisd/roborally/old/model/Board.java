/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.old.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.old.fileaccess.ISerializable;
import dk.dtu.compute.se.pisd.roborally.old.model.spaces.CheckPointSpace;
import dk.dtu.compute.se.pisd.roborally.old.model.spaces.Space;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.old.model.Phase.INITIALISATION;

/**
 * Shows the game board in the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Board extends Subject implements ISerializable {

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
    private int checkpointCount;

    /**
     * A constructor for internal use, which has literally all the parts of the Board given. Used for deserialization
     *
     * @param moveCounter
     * @param width
     * @param height
     * @param boardName
     * @param gameId
     * @param spaces
     * @param current
     * @param phase
     * @param step
     * @param stepMode
     * @param checkpointCount
     * @author Daniel Jensen
     */
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
        this.checkpointCount = 0;
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
     * Gets the given coordinates space on the board.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the given coordinates, or null if out of bounds
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public Space getSpace(Position position) {
        return getSpace(position.X, position.Y);
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
        if (player.board == this && !players.contains(player)) {
            players.add(player);
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

    // From 1.4.0
    /*public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }*/

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        return getSpace(Position.move(space.position, heading));
        /*
        int x = space.Position.X;
        int y = space.y;

        // TODO this will loop the players around to the other side of the board
        switch (heading) {
            case SOUTH -> y = (y + 1) % height;
            case WEST -> x = (x + width - 1) % width;
            case NORTH -> y = (y + height - 1) % height;
            case EAST -> x = (x + 1) % width;
        }

        return getSpace(x, y);
         */
    }

    // From 1.4.0
    /*public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }
        Heading reverse = Heading.values()[(heading.ordinal() + 2)% Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null) {
            if (result.getWalls().contains(reverse)) {
                return null;
            }
        }
        return result;
    }

}*/

    /**
     * @return the current move counter
     */
    public int getMoveCounter() {
        return moveCounter;
    }

    /**
     * increasing the move counter by 1
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
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Total Steps: " + getMoveCounter();
    }

    /**
     * A method to calculate all the moves coming from a single move.
     * This is used when performing a move, to make sure all the players who will get pushed also move.
     * It will also include the given move, but it has potentially been shortened (e.g. if there is a wall in the way).
     *
     * @param move the base move.
     * @return all the moves to be performed to execute the given move to the rules' satisfaction.
     * @author Daniel Jensen
     */
    public ArrayList<Move> resultingMoves(Move move) {
        int moveAmount = 0;
        ArrayList<Move> moves = new ArrayList<>();

        Space space = getSpace(move.Start);
        for (int i = 0; i < move.Amount; i++) {
            if (space.hasWall(move.Direction)) {
                break;
            }

            space = getNeighbour(space, move.Direction);
            if (space == null) {
                moveAmount++;
                break;
            } else if (space.hasWall(Heading.turnAround(move.Direction))) {
                break;
            } else if (space.getPlayer() != null) {
                // Can maximally move the full amount, minus the part already moved
                int moveOtherAmount = move.Amount - moveAmount;
                Move otherPlayerMove = new Move(space.position, move.Direction, moveOtherAmount, space.getPlayer());

                int leftToMove = 0;
                for (Move resultingMove : resultingMoves(otherPlayerMove)) {
                    moves.add(resultingMove);
                    leftToMove = Math.max(leftToMove, resultingMove.Amount);
                }
                moveAmount += leftToMove;

                break;
            }
            moveAmount++;
        }

        moves.add(new Move(move.Start, move.Direction, moveAmount, move.Moving));
        return moves;
    }

    public void addCheckpoint(Position position) {
        this.spaces[position.X][position.Y] = new CheckPointSpace(position, checkpointCount++);
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

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("width", this.width);
        jsonObject.addProperty("height", this.height);
        jsonObject.addProperty("boardName", this.boardName);
        jsonObject.addProperty("gameId", this.gameId);
        JsonArray jsonArrayPlayers = new JsonArray();

        for (Player player : this.players) {
            jsonArrayPlayers.add(player.serialize());
        }
        jsonObject.add("players", jsonArrayPlayers);

        jsonObject.addProperty("playerCount", this.getPlayerCount());
        jsonObject.addProperty("currentPlayer", this.current.getName());
        jsonObject.addProperty("checkPointCount", checkpointCount);
        jsonObject.addProperty("moveCounter", this.moveCounter);
        jsonObject.addProperty("step", this.step);
        jsonObject.addProperty("phase", this.phase.toString());
        jsonObject.addProperty("stepMode", this.stepMode);

        JsonArray jsonArraySpaces = new JsonArray();
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Space currentSpace = spaces[i][j];
                // TODO
                jsonArraySpaces.add(currentSpace.serialize());
            }
        }

        jsonObject.add("spaces", jsonArraySpaces);

        return jsonObject;
    }

    @Override
    public ISerializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        int width = jsonObject.get("width").getAsInt();
        int height = jsonObject.get("height").getAsInt();

        String boardName = jsonObject.get("boardName").getAsString();
        JsonElement gameIdJson = jsonObject.get("gameId");
        Integer gameId = (gameIdJson == null) ? null : gameIdJson.getAsInt();

        int playerCount = jsonObject.get("playerCount").getAsInt();

        // Adding players
        JsonArray playersJson = jsonObject.get("players").getAsJsonArray();
        Player playerToAdd = new Player(null, null, "");
        ArrayList<Player> players = new ArrayList<>();

        ArrayList<Position> playerPositions = new ArrayList<>();
        Position position = new Position(0, 0);
        for (int i = 0; i < playerCount; i++) {
            JsonObject playerJson = playersJson.get(i).getAsJsonObject();
            playerToAdd = (Player) playerToAdd.deserialize(playerJson);
            players.add(playerToAdd);

            playerPositions.add((Position) position.deserialize(playerJson.get("space")));
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

        int checkpointCount = jsonObject.get("checkPointCount").getAsInt();
        int moveCounter = jsonObject.get("moveCounter").getAsInt();
        int step = jsonObject.get("step").getAsInt();
        Phase phase = Phase.valueOf(jsonObject.get("phase").getAsString());
        boolean stepMode = jsonObject.get("stepMode").getAsBoolean();

        Space[][] spaces = new Space[width][height];
        JsonArray spacesJson = jsonObject.get("spaces").getAsJsonArray();
        Space space = new Space(new Position(0, 0));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                JsonObject spaceJson = spacesJson.get(x * height + y).getAsJsonObject();
                spaces[x][y] = (Space) space.deserialize(spaceJson);

                JsonElement playerNameJson = spaceJson.get("playerOccupyingSpace");
                Player player = null;
                if (playerNameJson != null) {
                    String playerName = playerNameJson.getAsString();
                    for (Player candidate : players) {
                        if (candidate.getName().equals(playerName)) {
                            player = candidate;
                            break;
                        }
                    }
                }

                spaces[x][y].setPlayer(player);
            }
        }

        Board board = new Board(moveCounter, width, height, boardName, gameId, spaces, current, phase, step, stepMode, checkpointCount);
        for (int i = 0; i < playerCount; i++) {
            Player p = players.remove(0);
            Position pPos = playerPositions.get(i);
            p.setSpace(board.getSpace(pPos));

            board.addPlayer(p.copy(board));
        }

        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }
}

    


