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

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;

/**
 * A class representing a Command Card Field, that extends the Subject class.
 * Command Card Field represents a field where a Command Card can be placed by a Player.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class CommandCardField extends Subject  {

    /**
     * The Player associated with this Command Card Field.
     */
    final public PlayerType player;

    /**
     * The class represent a command card, that exstends the Subject class.
     * Command Card represents a specific command that can be executed in the game
     */
    private CommandCard card;

    private boolean visible;

    /**
     * Constructs a new CommandCardField with the given Player.
     *
     * @param player The Player associated with this Command Card Field.
     */
    public CommandCardField(PlayerType player) {
        this.player = player;
        this.card = null;
        this.visible = true;
    }

    /**
     * Returns the Command Card currently placed in this Command Card Field.
     *
     * @return The Command Card currently placed in this field, or null if no Command Card is placed.
     */
    public CommandCard getCard() {
        return card;
    }

    /**
     * Sets the Command Card to be placed in this Command Card Field.
     * If the new Command Card is different from the current Command Card,
     * it will notify observers of the change.
     *
     * @param card placeing the command card in the field.
     */
    public void setCard(CommandCard card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }

    /**
     * Returns whether the Command Card in this field is currently visible.
     *
     * @return true if the Command Card is visible, false otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility of the Command Card in this field.
     * If the visibility changes, it will notify observers of the change.
     *
     * @param visible true to make the command card visible, false to make it invisible.
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }
    
}
