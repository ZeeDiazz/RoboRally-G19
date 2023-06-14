package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Move;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlueConveyorSpaceTest {
    private Robot robot;

    @BeforeEach
    void setUp() {
        robot = new Robot("Green",null);
    }

    @Test
    void testIfBlueConveyorSpaceMoveRobot() {
        Space blueConveyorSpace = new BlueConveyorSpace(new Position(1,1),HeadingDirection.NORTH);
        Move move = blueConveyorSpace.endedRegisterOn(robot,0);
        assertTrue(move.amount == 2);
    }

}