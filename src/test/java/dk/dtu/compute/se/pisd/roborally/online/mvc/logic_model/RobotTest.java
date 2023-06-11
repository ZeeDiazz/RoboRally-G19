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
        //A robot start by have South as a Heading direction
        assertEquals(HeadingDirection.SOUTH, robot.getHeadingDirection());

        //Make turn to their left
        robot.setHeadingDirection(HeadingDirection.leftHeadingDirection(robot.getHeadingDirection()));

        //Check if the robot is looking towards East
        assertEquals(HeadingDirection.EAST, robot.getHeadingDirection());
    }

    @Test
    void getRebootPosition() {
        //Robot doesn't have a rebootPosition to begin with
        assertNull(robot.getRebootPosition());

        //Robot reboot position is 1,0
        Position rebootPosition = new Position(1, 0);
        robot.setRebootPosition(rebootPosition);

        //Check if the Robot rebootPosition is updated to the new rebootPosition
        assertEquals(rebootPosition, robot.getRebootPosition());
    }

    @Test
    void getColor() {
        //We assigned the color Green the robot
        assertEquals("Green", robot.getColor());

        //Change the players color to blue
        robot.setColor("Blue");

        //Check if the color is changed
        assertEquals("Blue", robot.getColor());
    }

    @Test
    void getOwner() {
        //Check if the player(owner) is the owner of this robot
        assertEquals(owner, robot.getOwner());
    }
}