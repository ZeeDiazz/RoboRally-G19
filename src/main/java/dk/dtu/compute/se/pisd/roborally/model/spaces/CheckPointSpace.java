package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public class CheckPointSpace extends Space implements SubClassOfSpace {
    public final int id;

    public CheckPointSpace(Position position, int id, Heading... walls) {
        super(position, walls);

        this.id = id;
    }

    /**
     * @author Daniel Jensen
     * Check whether a player has passed this checkpoint
     * @param player The player to check
     * @return True if the player has passed this checkpoint, else false.
     */
    public boolean hasPassed(Player player) {
        return player.checkpointGoal < this.id;
    }

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        if (!hasPassed(player)) {
            player.checkpointGoal = this.id + 1;
            // player.setRebootSpace(this);
            changed();
        }
        return null;
    }

    @Override
    public Space copy(Position newPosition) {
        return new CheckPointSpace(newPosition, this.id, this.walls.toArray(new Heading[0]));
    }
}
