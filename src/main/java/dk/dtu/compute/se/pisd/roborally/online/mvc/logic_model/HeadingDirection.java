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
package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum HeadingDirection {
    SOUTH,
    WEST,
    NORTH,
    EAST;

    /**
     * Gives the right direction of the original direction
     *
     * @param heading
     * @return
     * @author Daniel
     */
    public static HeadingDirection rightHeadingDirection(HeadingDirection heading) {
        HeadingDirection newDirection;
        switch (heading) {
            case SOUTH -> newDirection = WEST;
            case WEST -> newDirection = NORTH;
            case NORTH -> newDirection = EAST;
            case EAST -> newDirection = SOUTH;
            default -> newDirection = heading;
        }
        return newDirection;
    }

    /**
     * Gives the left direction of the original direction
     *
     * @param heading
     * @return The left direction of the original direction
     * @author Daniel
     */
    public static HeadingDirection leftHeadingDirection(HeadingDirection heading) {
        HeadingDirection newDirection;
        switch (heading) {
            case SOUTH -> newDirection = EAST;
            case WEST -> newDirection = SOUTH;
            case NORTH -> newDirection = WEST;
            case EAST -> newDirection = NORTH;
            default -> newDirection = heading;
        }
        return newDirection;
    }

    /**
     * Gives the opposite direction of the original direction
     *
     * @param heading
     * @return The opposite direction of the original direction
     * @author ZeeDiazz (Zaid)
     */
    public static HeadingDirection oppositeHeadingDirection(HeadingDirection heading) {
        HeadingDirection newDirection;
        switch (heading) {
            case SOUTH -> newDirection = NORTH;
            case WEST -> newDirection = EAST;
            case NORTH -> newDirection = SOUTH;
            case EAST -> newDirection = WEST;
            default -> newDirection = heading;
        }
        return newDirection;
    }
}
