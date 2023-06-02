package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Move;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;

public abstract class GearSpace extends Space {

    /**
     *  Represents a gear space that can be occupied by a player.
     *
     * @param position the position of the gear space
     * @param walls an array of Heading values representing the walls surrounding the gear space
     */
    public GearSpace(Position position, HeadingDirection... walls) {
        super(position, walls);
    }

    /**
     * Turns the player occupying this gear space.
     *
     * @param robot the player to turn
     */
    protected abstract void turnRobot(Robot robot);

    /**
     * Called when a player finishes registering on this gear space.
     * Turns the player and updates the state of the gear space.
     *
     * @param robot the player who finished registering
     * @param registerIndex the index of the register that the player finished on
     * @return always returns null
     */
    @Override
    public Move endedRegisterOn(Robot robot, int registerIndex) {
        turnRobot(robot);
        changed();
        return null;
    }
}
