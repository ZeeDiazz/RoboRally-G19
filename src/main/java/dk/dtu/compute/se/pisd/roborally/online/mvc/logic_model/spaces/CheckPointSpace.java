package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;

public class CheckPointSpace extends Space {
    public final int id;

    public CheckPointSpace(Position position, int id, HeadingDirection... walls) {
        super(position, walls);

        this.id = id;
    }

    /**
     * @param robot The player to check
     * @return True if the player has passed this checkpoint, else false.
     * @author Daniel Jensen
     * Check whether a player has passed this checkpoint
     */
    public boolean hasPassed(Robot robot) {
        return robot.checkpointReached >= this.id;
    }

    @Override
    public Move endedRegisterOn(Robot robot, int registerIndex) {
        if (hasPassed(robot)) {
            robot.checkpointReached = this.id + 1;
            // player.setRebootSpace(this);
            changed();
        }
        return null;
    }

    @Override
    public Space copy(Position newPosition) {
        return new CheckPointSpace(newPosition, this.id, this.walls.toArray(new HeadingDirection[0]));
    }

}
