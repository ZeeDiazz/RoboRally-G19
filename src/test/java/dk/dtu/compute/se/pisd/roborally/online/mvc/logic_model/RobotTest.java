package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {
    private Robot robot;
    private Player owner;

    @BeforeEach
    void setUp() {
        robot = new Robot("Green",owner);
    }

    @Test
    void getSpace() {
        //The robot isn't assigned a Space yet
        Space startingRobotSpace = robot.getSpace();
        assertNull(startingRobotSpace);

        //Place the robot at the space 1,0
        Space space = new Space(new Position(1, 0));
        robot.setSpace(space);

        //Check if the player is on that space
        assertEquals(space, robot.getSpace());

        //Check if the player no longer is
        assertNotEquals(startingRobotSpace,robot.getSpace());
    }

    @Test
    void getHeadingDirection() {
    }

    @Test
    void getRebootPosition() {
    }

    @Test
    void getColor() {
    }

    @Test
    void getOwner() {
    }
}