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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Represent the Space of the board, and extends Subject
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {
    public final Board board;

    public final int x;
    public final int y;

    private Player player;

    private final ArrayList<Heading> walls;


    /**
     * Construct a new Space object at the specified board, x-coordinate and y-coordinate.
     * @param board the game board the space belong to.
     * @param x x-coordinate of the space on the board.
     * @param y y-coordinate of the space on the board.
     */
    public Space(Board board, int x, int y) {
        this(board, x, y, new Heading[0]);
    }

    /**
     * @author Daniel Jensen
     * Construct a new Space object at the specified board, x-coordinate and y-coordinate.
     * @param board the game board the space belong to.
     * @param x x-coordinate of the space on the board.
     * @param y y-coordinate of the space on the board.
     * @param walls The walls on this space
     */
    public Space(Board board, int x, int y, Heading... walls) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        this.walls = new ArrayList<>(Arrays.stream(walls).toList());
    }

    /**
     * Gets the player currently occupying this space.
     * @return The player on the space, and null if the space is unoccupied
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player currently occupying this space.
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
     * @return True if there is a player on this space, else false.
     */
    public boolean hasPlayer() {
        return getPlayer() != null;
    }

    /**
     * @author Daniel Jensen
     * Add a wall to this space
     * @param direction The direction to place the wall
     */
    public void addWall(Heading direction) {
        if (!hasWall(direction)) {
            walls.add(direction);
            notifyChange();
        }
    }

    /**
     * @author Zahedullah Wafa
     * Indicating whether this space has a wall in the given direction
     * @param direction The direction to check
     * @return True if this space has a wall that way
     */
    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    /**
     * @author Zahed Wafa
     * This method checks whether it's possible to make a legal move
     * @param heading The direction in which way the player wants to move in
     * @return Returns true if the move is legal and false if not
     */

    public boolean canMove(Heading heading) {
        if(hasWall(heading)) {
            return false;
        }
        Space neighbour = board.getNeighbour(this, heading);
        if (neighbour.hasWall(Heading.turnAround(heading))) {
            return false;
        }
        if (neighbour.getPlayer() == null) {
            return true;
        }
        return neighbour.canMove(heading);
    }

    /**
     * HACK
     */
    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
