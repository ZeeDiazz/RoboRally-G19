package dk.dtu.compute.se.pisd.roborally.model;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ISerializable;

public class CheckPoint extends Space {
    public final int Id;

    /**
     * @param board the game board the space belong to.
     * @param x     x-coordinate of the space on the board.
     * @param y     y-coordinate of the space on the board.
     * @param Id    counter for order of checkpoints
     * @author Felix Schmidt, s224313@dtu.dk
     * Constructor for CheckPoint
     */
    public CheckPoint(Board board, int x, int y, int Id) {
        super(board, x, y);
        this.Id = Id;
    }

    /**
     * @param player The player to check
     * @return True if the player has passed this checkpoint, else false.
     * @author Daniel Jensen
     * Check whether a player has passed this checkpoint
     */
    public boolean hasPassed(Player player) {
        return player.checkpointGoal < this.Id;
    }

    /**
     * @param player The player who passed the checkpoint
     * @author Daniel Jensen
     * Called when a player has passed this checkpoint. It will set the next target for the player.
     */
    public void playerPassed(Player player) {
        if (!hasPassed(player)) {
            player.checkpointGoal = this.Id + 1;
            player.setRebootSpace(this);
        }
    }

    @Override
    public JsonElement serialize() {

        JsonObject jsonObject = super.serialize().getAsJsonObject();


        jsonObject.addProperty("checkpointId", this.Id);


        return jsonObject;
    }
}
