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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import org.jetbrains.annotations.NotNull;

/**
 * The class represent a command card, that exstends the Subject class.
 * Command Card represents a specific command that can be executed in the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class CommandCard extends Subject implements Serializable {
    /**
     * The Command associated with this Command Card.
     *
     * @author Zahedullah
     */
    final public Command command;

    /**
     * Constructs a new command card, with the given command.
     *
     * @param command The command associated with this Command Card.
     * @author Zahedullah
     */
    public CommandCard(@NotNull Command command) {
        this.command = command;
    }

    /**
     * Returns the command name associated with the command card.
     *
     * @return the name of the command card.
     * @author Zahedullah
     */
    public String getName() {
        return command.displayName;
    }


    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("command", this.command.toString());

        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject json = element.getAsJsonObject();

        Command command = Command.valueOf(json.get("command").getAsString());

        return new CommandCard(command);
    }
}
