package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckPointSpaceTest {
    private Robot robot;
    @BeforeEach
    void setUp() {
        robot = new Robot("Green",null);
    }

    @Test
    void testIfPointsReachedIncrementsWhenCheckPointReached(){
        CheckPointSpace checkPointSpace = new CheckPointSpace(null, 0);

        checkPointSpace.endedRegisterOn(robot, 0);
        assertTrue(robot.checkpointsReached == 1);
    }
    @Test
    void testThatCheckPointsReachedDoesNotIncrementWhenIdIsGreater(){
        CheckPointSpace checkPointSpace = new CheckPointSpace(null, 1);

        checkPointSpace.endedRegisterOn(robot, 0);
        assertTrue(robot.checkpointsReached == 0);
    }
    @Test
    void testIfRobotHasPassedCheckPoint(){
        CheckPointSpace checkPointSpace = new CheckPointSpace(null, 0);

        checkPointSpace.endedRegisterOn(robot, 0);
        assertTrue(checkPointSpace.hasPassed(robot));
    }
    @Test
    void testIfRobotHasNotPassedCheckPoint(){
        CheckPointSpace checkPointSpace = new CheckPointSpace(null, 1);

        checkPointSpace.endedRegisterOn(robot, 0);
        assertFalse(checkPointSpace.hasPassed(robot));
    }


}