package dk.dtu.compute.se.pisd.roborally.old.model.spaces;

import dk.dtu.compute.se.pisd.roborally.old.model.Heading;
import dk.dtu.compute.se.pisd.roborally.old.model.Move;
import dk.dtu.compute.se.pisd.roborally.old.model.Player;
import dk.dtu.compute.se.pisd.roborally.old.model.Position;

public abstract class GearSpace extends Space {

    /**
     *  Represents a gear space that can be occupied by a player.
     *
     * @param position the position of the gear space
     * @param walls an array of Heading values representing the walls surrounding the gear space
     */
    public GearSpace(Position position, Heading... walls) {
        super(position, walls);
    }

    /**
     * Turns the player occupying this gear space.
     *
     * @param player the player to turn
     */
    protected abstract void turnPlayer(Player player);

    /**
     * Called when a player finishes registering on this gear space.
     * Turns the player and updates the state of the gear space.
     *
     * @param player the player who finished registering
     * @param registerIndex the index of the register that the player finished on
     * @return always returns null
     */
    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        turnPlayer(player);
        changed();
        return null;
    }
}
