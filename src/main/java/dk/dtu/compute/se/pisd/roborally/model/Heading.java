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

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum Heading {
    SOUTH,
    WEST,
    NORTH,
    EAST;

    // From 1.4.0
    /* public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }*/

    public static Heading turnRight(Heading heading) {
        Heading newDirection;
        switch (heading) {
            case SOUTH -> newDirection = WEST;
            case WEST -> newDirection = NORTH;
            case NORTH -> newDirection = EAST;
            case EAST -> newDirection = SOUTH;
            default -> newDirection = heading;
        }
        return newDirection;
    }

    public static Heading turnLeft(Heading heading) {
        Heading newDirection;
        switch (heading) {
            case SOUTH -> newDirection = EAST;
            case WEST -> newDirection = SOUTH;
            case NORTH -> newDirection = WEST;
            case EAST -> newDirection = NORTH;
            default -> newDirection = heading;
        }
        return newDirection;
    }

    public static Heading turnAround(Heading heading) {
        Heading newDirection;
        switch (heading) {
            case SOUTH -> newDirection = NORTH;
            case WEST -> newDirection = EAST;
            case NORTH -> newDirection = SOUTH;
            case EAST -> newDirection = WEST;
            default -> newDirection = heading;
        }
        return newDirection;
    }

    public static Heading getHeading(String name) {

        for (Heading heading : Heading.values()) {
            if (name.equals(heading.toString())) {
                return heading;
            }
        }
        return null;
    }

}
