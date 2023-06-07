package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Move;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;

public class PitSpace extends Space {

    /**
     * Represents a pit space that can trap a robot, forcing them to reboot.
     *
     * @param position the position of the pit space
     * @param walls an array of Heading values representing the walls surrounding the pit space
     */
    public PitSpace(Position position, HeadingDirection... walls) {
        super(position, walls);
    }

    /**
     * Called when a robot lands on this pit space.
     * Checks if the player has the upgrade to avoid being trapped and reboots them.
     *
     * @param robot the robot who landed on the pit space
     */
    @Override
    public void landedOn(Robot robot) {
        // TODO check if they have the upgrade
        robot.reboot();
        changed();
    }

    /**
     * Called when a player finishes registering on this pit space.
     * Reboots the player and updates the state of the pit space.
     *
     * @param robot the player who finished registering
     * @param registerIndex the index of the register that the player finished on
     * @return always returns null
     */
    @Override
    public Move endedRegisterOn(Robot robot, int registerIndex) {
        robot.reboot();
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
        return new PitSpace(newPosition, walls.toArray(new HeadingDirection[0]));
    }
}
