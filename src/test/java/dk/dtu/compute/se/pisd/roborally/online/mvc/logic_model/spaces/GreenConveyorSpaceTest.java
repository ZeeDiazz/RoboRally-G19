package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Move;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreenConveyorSpaceTest {
    private Robot robot;

    @BeforeEach
    void setUp() {
        robot = new Robot("Green",null);
    }

    @Test
    void testIfGreenConveyorSpaceMoveRobot() {
        Space blueConveyorSpace = new GreenConveyorSpace(new Position(1,1),HeadingDirection.NORTH);
        Move move = blueConveyorSpace.endedRegisterOn(robot,0);
        assertTrue(move.amount == 1);
    }

}