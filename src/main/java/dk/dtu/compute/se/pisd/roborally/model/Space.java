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

import javafx.geometry.Pos;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction; // From 1.4.0

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Represent the Space of the board, and extends Subject
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Space extends Subject {
    public final Position Position;
    private Player player;
    private final ArrayList<Heading> walls;
    private List<FieldAction> actions;
    public final boolean IsSpawnSpace;

    /**
     * Construct a new Space object at the specified position.
     * @param position the position of the new space
     * @param isSpawnSpace whether this space is one of the original spawn points
     */
    public Space(Position position, boolean isSpawnSpace) {
        this(position, isSpawnSpace, new Heading[0]);
    }

    public Space(Position position, boolean isSpawnSpace, Heading... walls) {
        this.Position = position;
        this.IsSpawnSpace = isSpawnSpace;
        this.actions = new ArrayList<>();
        this.player = null;
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
        if (player != oldPlayer) {
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
    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public Space copy(Position newPosition) {
        return new Space(newPosition, this.IsSpawnSpace, this.walls.toArray(new Heading[0]));
    }

    public void rotateLeft() {
        int wallCount = walls.size();
        for (int i = 0; i < wallCount; i++) {
            Heading wall = walls.remove(0);
            wall = Heading.turnLeft(wall);
            walls.add(wall);
        }
    }
}
