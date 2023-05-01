package dk.dtu.compute.se.pisd.roborally.model;


public class CheckPoint extends Space {
    public final int Id;

    /**
    * @author Felix Schmidt, s224313@dtu.dk
    * Constructor for CheckPoint
    * @param position the position of the space on the board
    * @param id counter for order of checkpoints
    */
    public CheckPoint(Position position, int id){
        super(position, false);
        this.Id = id;
    }

    /**
     * @author Daniel Jensen
     * Check whether a player has passed this checkpoint
     * @param player The player to check
     * @return True if the player has passed this checkpoint, else false.
     */
    public boolean hasPassed(Player player) {
        return player.checkpointGoal < this.Id;
    }

    /**
     * @author Daniel Jensen
     * Called when a player has passed this checkpoint. It will set the next target for the player.
     * @param player The player who passed the checkpoint
     */
    public void playerPassed(Player player) {
        if (!hasPassed(player)) {
            player.checkpointGoal = this.Id + 1;
            player.setRebootSpace(this);
        }
    }
}
