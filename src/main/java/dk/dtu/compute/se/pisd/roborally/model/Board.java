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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static dk.dtu.compute.se.pisd.roborally.model.ObstacleType.*;
import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * Shows the game board in the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Board extends Subject {
    private int moveCounter;

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    /**
     * Creates a new board with the given board name, width and height. Also a construtor for Board, which also creates spaces and obstacles
     *
     * @param boardName the name of the board
     * @param width     the width of the board
     * @param height    the height of the board
     * @author ZeeDiazz (Zaid)
     */

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space;

                //ZeeDiazz (Zaid) {
                if (x == 0 && y == 1 || x == 2 && y == 3) {
                    space = new Obstacle(this, x, y, BLUE_CONVEYOR_BELT, Heading.SOUTH);
                } else if (x == 1 && y == 5) {
                    space = new Obstacle(this, x, y, GREEN_CONVEYOR_BELT, Heading.NORTH);
                }

                //   }
                else if (x == 3 && y == 4) {
                    space = new CheckPoint(this, x, y, 0);
                } else if (x == 6 && y == 2) {
                    space = new CheckPoint(this, x, y, 1);
                } else {
                    space = new Space(this, x, y);
                }


                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
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
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Gets the correct number of the player in the game
     *
     * @return number of player
     */
    public int getPlayersNumber() {
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
     * @param i index of the player
     * @return the players index, else null if the index is out of bounds
     */

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
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
     * @return the current step
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
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

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

        return getSpace(x, y);
    }

    /**
     * Gives the current move counter
     *
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
                ", Step: " + getMoveCounter();
    }


}
