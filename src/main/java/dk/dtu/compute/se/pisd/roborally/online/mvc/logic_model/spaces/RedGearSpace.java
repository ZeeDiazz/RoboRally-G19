package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;

public class RedGearSpace extends GearSpace {
    /**
     * Represents a space on the board with a red gear that rotates players left when they end their turn on it.
     */
    public RedGearSpace(Position position, HeadingDirection direction, HeadingDirection... walls) {
        super(position,direction, walls);
    }

    /**
     * Rotates the given robot left when they end their turn on this space.
     *
     * @param robot the robot to rotate left
     */
    @Override
    protected void turnRobot(Robot robot) {
        robot.setHeadingDirection(HeadingDirection.leftHeadingDirection(robot.getHeadingDirection()));
    }

    /**
     * Creates a new instance of this space with the given position and walls.
     *
     * @param newPosition the new position of the space
     * @return a new instance of this space with the given position and walls
     */
    @Override
    public Space copy(Position newPosition) {
        return new RedGearSpace(newPosition,direction, this.walls.toArray(new HeadingDirection[0]));
    }
}
