package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckPointSpaceTest {

    @Test
    void checkPointReachedGoalIncreaseAndStatusChange() {
        CheckPointSpace checkPointSpace = new CheckPointSpace(new Position(0,0),0, Heading.SOUTH);
        Player player = new Player(null, "blue", "Zaid");
        player.setSpace(checkPointSpace);
        checkPointSpace.endedRegisterOn(player,1);
        Assertions.assertTrue(player.checkpointGoal ==  1);
        Assertions.assertTrue(checkPointSpace.hasPassed(player));

    }
}