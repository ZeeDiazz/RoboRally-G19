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
package dk.dtu.compute.se.pisd.roborally.model.spaces.legacy;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Position;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Represent the Space of the board, and extends Subject
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Space extends Subject {

    public final Board board;

    public final dk.dtu.compute.se.pisd.roborally.model.Position Position;

    private Player player;

    private final ArrayList<Heading> walls;

    // From 1.4.0

    private List<FieldAction> actions = new ArrayList<>();

    /**
     * Construct a new Space object at the specified board, x-coordinate and y-coordinate.
     *
     * @param board the game board the space belong to.
     * @param x     x-coordinate of the space on the board.
     * @param y     y-coordinate of the space on the board.
     */
    public Space(Board board, int x, int y) {
        this(board, x, y, new Heading[0]);
    }

    /**
     * @param board the game board the space belong to.
     * @param x     x-coordinate of the space on the board.
     * @param y     y-coordinate of the space on the board.
     * @param walls The walls on this space
     * @author Daniel Jensen
     * Construct a new Space object at the specified board, x-coordinate and y-coordinate.
     */
    public Space(Board board, int x, int y, Heading... walls) {
        this.board = board;
        this.Position = new Position(x, y);
        player = null;
        this.walls = new ArrayList<>(Arrays.stream(walls).toList());
    }

    /**
     * Gets the player currently occupying this space.
     *
     * @return The player on the space, and null if the space is unoccupied
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player currently occupying this space.
     *
     * @param player The player to set as occupying this space.
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    /**
     * Whether this space currently has a player occupying it
     *
     * @return True if there is a player on this space, else false.
     */
    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    /**
     * @param direction The direction to place the wall
     * @author Daniel Jensen
     * Add a wall to this space
     */
    public void addWall(Heading direction) {
        if (!hasWall(direction)) {
            walls.add(direction);
            notifyChange();
        }
    }

    /**
     * @param direction The direction to check
     * @return True if this space has a wall that way
     * @author Zahedullah Wafa
     * Indicating whether this space has a wall in the given direction
     */
    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    /**
     * @param heading The direction in which way the player wants to move in
     * @return Returns true if the move is legal and false if not
     * @author Zahed Wafa
     * This method checks whether it's possible to make a legal move
     */
    public boolean canMove(Heading heading) {
        if (hasWall(heading)) {
            return false;
        }
        Space neighbour = board.getNeighbour(this, heading);
        // No neighbor means going off the board, which is always allowed
        if (neighbour == null) {
            return true;
        }
        // If the neighbor has a wall towards this space, it's equivalent to having a wall from this space towards the neighbor
        if (neighbour.hasWall(Heading.turnAround(heading))) {
            return false;
        }
        // If the neighbor has no players, you can always move to it
        if (!neighbour.hasPlayer()) {
            return true;
        }
        // If there is a player on neighboring square, you can only move if they can move
        return neighbour.canMove(heading);
    }

    // From 1.4.0
    public List<Heading> getWalls() {
        return walls;
    }

    public List<FieldAction> getActions() {
        return actions;
    }


    /**
     * HACK
     */
    public void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
