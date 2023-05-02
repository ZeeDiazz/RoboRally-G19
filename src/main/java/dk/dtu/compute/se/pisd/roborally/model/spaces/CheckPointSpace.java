package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class CheckPointSpace extends EmptySpace {
    public final int id;

    public CheckPointSpace(Board board, Position position, int id, Heading... walls) {
        super(board, position, walls);

        this.id = id;
    }

    public CheckPointSpace(Board board, Position position, int id) {
        this(board, position, id, new Heading[0]);
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
    public void endedRegisterOn(Player player, int registerIndex) {
        if (!hasPassed(player)) {
            player.checkpointGoal = this.id + 1;
            // player.setRebootSpace(this);
            changed();
        }
    }
}
