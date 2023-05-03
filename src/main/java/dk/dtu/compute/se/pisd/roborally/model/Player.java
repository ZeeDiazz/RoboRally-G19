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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ISerializable;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * Player class represent a player in the board game, that extends Subject.
 * The class got information about the player
 * (name, color, position on the board heading direction, and command card fields.)
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Player extends Subject implements ISerializable {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    public Board board;
    public int checkpointGoal = 0;
    private int energyCube;

    private String name;
    private String color;

    private Space space;
    private Position rebootPosition;
    private Heading heading = SOUTH;


    private CommandCardField[] program;
    private CommandCardField[] cards;
    private Command prevProgramming;

    /**
     * Constructor to create a Player object with the given board, color, and name.
     *
     * @param board The board that the player belong to.
     * @param color The color of the player.
     * @param name  The name of the player.
     */
    public Player(Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        //Player starts with 5 energy cube
        this.energyCube = 5;

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
     *
     * @return players name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the player
     *
     * @param name Sets the players name
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.changed();
            }
        }
    }

    /**
     * Gets the color of the player
     *
     * @return the color of the player
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets color of the player
     *
     * @param color Sets the players color
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.changed();
        }
    }

    /**
     * Gets the position of the player on the board.
     *
     * @return position of the player.
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Sets the space where the player is positioned.
     *
     * @param space the space to set the players position.
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace) {
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
     *
     * @return the heading direction of the player
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sets the absalute direction of the player.
     *
     * @param heading the new direction (heading) to be set.
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.changed();
            }
        }
    }

    /**
     * Gets the program field at the specific index
     *
     * @param index index og the program field
     * @return the command card field at the specific index
     */
    public CommandCardField getProgramField(int index) {
        return program[index];
    }

    /**
     * Gets the program field at the specific index
     *
     * @param index index og the program field
     * @return the command card field at the specific index
     */
    public CommandCardField getCardField(int index) {
        return cards[index];
    }

    /**
     * @param space The space the player will reboot on
     * @author Daniel Jensen
     * Set the reboot space of a player, used when the player has to reboot
     */
    public void setRebootPosition(Position position) {
        this.rebootPosition = position;
    }

    public Position getRebootPosition() {
        return rebootPosition;
    }

    public CommandCardField[] getCards() {
        return cards;
    }

    public CommandCardField[] getProgram() {
        return program;
    }

    /**
     * @author Daniel Jensen
     * Reboot the player, setting their position to their reboot space (latest collected checkpoint)
     */
    public void reboot() {
        // TODO fix
        notifyChange();
    }

    /**
     * @author ZeeDiazz (Zaid)
     * gets the amount of energy cubes a player has
     * @return the amount of energy cubes
     */
    public int getEnergyCube(){return energyCube;}

    /**
     * @author Zeediazz (Zaid)
     * Takes an amount of energy cube and adds to Players energy cubes
     * If amount is less or equal to 0 it does nothing
     * @param amount of energy cube
     */
    public void addEnergyCube(int amount){
        if(amount > 0) {
            energyCube += amount;
            notifyChange();
        }
    }

    /**
     * @author ZeeDiazz (Zaid)
     * Takes an amount of energy cube and removes X amount of Players energy cubes
     * If amount is less or equal to 0 it does nothing
     * @param amount
     */
    public void removeEnergyCube(int amount){
        if(amount > 0) {
            energyCube -= amount;
            notifyChange();
        }
    }

    /**
     * @author ZeeDiazz (Zaid)
     * Get the programming from previous register
     * @return
     */
    public Command getPrevProgramming() {
        return prevProgramming;
    }

    /**
     * @author ZeeDiazz (Zaid)
     * Set a programming as previous programming
     * @param programming
     */
    public void setPrevProgramming(Command programming) {
       prevProgramming = programming;
     }
       
    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("checkpointGoal", this.checkpointGoal);
        jsonObject.addProperty("color", this.color);
        jsonObject.add("space", this.space.position.serialize());
        jsonObject.add("rebootSpace", this.rebootPosition.serialize());
        jsonObject.addProperty("heading", this.heading.toString());

        if (this.prevProgramming != null) {
            jsonObject.addProperty("previousCommand", this.prevProgramming.displayName);
        }

        JsonArray jsonArrayProgram = new JsonArray();
        for (CommandCardField cardField : program) {
            jsonArrayProgram.add(cardField.serialize());
        }
        jsonObject.add("program", jsonArrayProgram);

        JsonArray jsonArrayCards = new JsonArray();
        for (CommandCardField card : cards) {
            jsonArrayCards.add(card.serialize());
        }
        jsonObject.add("cards", jsonArrayCards);

        return jsonObject;
    }

    @Override
    public ISerializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        Player player = new Player(null, jsonObject.get("color").getAsString(), jsonObject.get("name").getAsString());
        player.checkpointGoal = jsonObject.get("checkpointGoal").getAsInt();

        Position position = new Position(0, 0);
        player.setRebootPosition((Position)position.deserialize(jsonObject.get("rebootSpace")));
        
        String headingAsString = jsonObject.get("heading").getAsString();
        for (Heading heading : Heading.values()) {
            if (headingAsString.equals(player.heading.toString())) {
                player.heading = heading;
                break;
            }
        }

        CommandCardField field = new CommandCardField(null);
        int index = 0;
        for (JsonElement cardJson : jsonObject.get("cards").getAsJsonArray()) {
            CommandCardField savedField = (CommandCardField)field.deserialize(cardJson);

            player.getCardField(index).setCard(savedField.getCard());
            player.getCardField(index).setVisible(savedField.isVisible());
            index++;
        }

        JsonElement prevProgrammingJson = jsonObject.get("previousCommand");
        Command previous;
        if (prevProgrammingJson == null) {
            previous = null;
        }
        else {
            previous = Command.valueOf(jsonObject.get("previousCommand").getAsString());
        }
        player.setPrevProgramming(previous);

        return player;
    }
    
    
}
