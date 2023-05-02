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

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * Player class represent a player in the board game, that extends Subject.
 * The class got information about the player
 * (name, color, position on the board heading direction, and command card fields.)
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;
    public int checkpointGoal = 0;
    private String name;
    private String color;

    private Space space;
    private Space rebootSpace;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    /**
     * Constructor to create a Player object with the given board, color, and name.
     * @param board The board that the player belong to.
     * @param color The color of the player.
     * @param name The name of the player.
     */
    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * Gets the name of the player
     * @return players name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the player
     * @param name Sets the players name
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Gets the color of the player
     * @return the color of the player
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets color of the player
     * @param color Sets the players color
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Gets the position of the player on the board.
     * @return position of the player.
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Sets the space where the player is positioned.
     * @param space the space to set the players position.
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * Gets the heading direction of the player
     * @return the heading direction of the player
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sets the absalute direction of the player.
     * @param heading the new direction (heading) to be set.
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Gets the program field at the specific index
     * @param index index og the program field
     * @return  the command card field at the specific index
     */
    public CommandCardField getProgramField(int index) {
        return program[index];
    }

    /**
     * Gets the program field at the specific index
     * @param index index og the program field
     * @return the command card field at the specific index
     */
    public CommandCardField getCardField(int index) {
        return cards[index];
    }

    /**
     * @author Daniel Jensen
     * Set the reboot space of a player, used when the player has to reboot
     * @param space The space the player will reboot on
     */
    public void setRebootSpace(Space space) {
        this.rebootSpace = space;
    }

    /**
     * @author Daniel Jensen
     * Reboot the player, setting their position to their reboot space (latest collected checkpoint)
     */
    public void reboot() {
        setSpace(this.rebootSpace);
        notifyChange();
    }


}
