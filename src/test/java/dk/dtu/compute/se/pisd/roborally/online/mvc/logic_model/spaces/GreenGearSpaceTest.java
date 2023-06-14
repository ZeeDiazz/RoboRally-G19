package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreenGearSpaceTest {
    private Robot robot;
    private Player owner;
    @BeforeEach
    void setUp() {
        robot = new Robot("Green",owner);
    }

    @Test
    void testIfGreenGearSpaceTurnRobot() {
        GreenGearSpace greenGearSpace = new GreenGearSpace(new Position(1,1), HeadingDirection.NORTH);// set robot heading direction to south to test if it turns to west

        robot.setHeadingDirection(HeadingDirection.SOUTH);
        greenGearSpace.turnRobot(robot);
        System.out.println(robot.getHeadingDirection());

        assertTrue(robot.getHeadingDirection() == HeadingDirection.WEST);
    }
    @Test
    void testIfGreenGearSpaceTurnRobotFromWestToNorth() {
        GreenGearSpace greenGearSpace = new GreenGearSpace(new Position(1,1), HeadingDirection.NORTH);

        robot.setHeadingDirection(HeadingDirection.WEST);
        greenGearSpace.turnRobot(robot);
        System.out.println(robot.getHeadingDirection());

        assertTrue(robot.getHeadingDirection() == HeadingDirection.NORTH);
    }
}