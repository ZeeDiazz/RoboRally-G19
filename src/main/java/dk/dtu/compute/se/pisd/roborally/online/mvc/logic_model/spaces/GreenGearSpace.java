package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;

public class GreenGearSpace extends GearSpace {
    public GreenGearSpace(Position position, HeadingDirection... walls) {
        super(position, walls);
    }

    @Override
    protected void turnRobot(Robot robot) {
        robot.setHeadingDirection(HeadingDirection.rightHeadingDirection(robot.getHeadingDirection()));
    }

    @Override
    public Space copy(Position newPosition) {
        return new GreenGearSpace(newPosition, this.walls.toArray(new HeadingDirection[0]));
    }
}
