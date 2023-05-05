package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public class PitSpace extends Space {

    /**
     * Represents a pit space that can trap a player, forcing them to reboot.
     *
     * @param position the position of the pit space
     * @param walls an array of Heading values representing the walls surrounding the pit space
     */
    public PitSpace(Position position, Heading... walls) {
        super(position, walls);
    }

    /**
     * Called when a player lands on this pit space.
     * Checks if the player has the upgrade to avoid being trapped and reboots them.
     *
     * @param player the player who landed on the pit space
     */
    @Override
    public void landedOn(Player player) {
        // TODO check if they have the upgrade
        player.reboot();
        changed();
    }

    /**
     * Called when a player finishes registering on this pit space.
     * Reboots the player and updates the state of the pit space.
     *
     * @param player the player who finished registering
     * @param registerIndex the index of the register that the player finished on
     * @return always returns null
     */
    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        player.reboot();
        changed();
        return null;
    }

    /**
     * Creates a new pit space with the same walls at a different position.
     *
     * @param newPosition the new position of the pit space
     * @return a new PitSpace object with the same walls
     */
    @Override
    public Space copy(Position newPosition) {
        return new PitSpace(newPosition, walls.toArray(new Heading[0]));
    }
}
