package dk.dtu.compute.se.pisd.roborally.model;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ISerializable;

public class CheckPoint extends Space {
    public final int Id;

    /**
     * @param position the position of the space on the board
     * @param id       counter for order of checkpoints
     * @author Felix Schmidt, s224313@dtu.dk
     * Constructor for CheckPoint
     */
    public CheckPoint(Position position, int id) {
        super(position, false);
        this.Id = id;

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

    public ISerializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        return null;
        
    }

    public Space copy(Position newPosition) {
        return new CheckPoint(newPosition, this.Id);

    }
}
