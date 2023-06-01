package dk.dtu.compute.se.pisd.roborally.old.model.spaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.old.fileaccess.ISerializable;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.old.model.Heading;
import dk.dtu.compute.se.pisd.roborally.old.model.Move;
import dk.dtu.compute.se.pisd.roborally.old.model.Player;
import dk.dtu.compute.se.pisd.roborally.old.model.Position;

public class CheckPointSpace extends Space {
    public final int id;

    public CheckPointSpace(Position position, int id, Heading... walls) {
        super(position, walls);

        this.id = id;
    }

    /**
     * @param player The player to check
     * @return True if the player has passed this checkpoint, else false.
     * @author Daniel Jensen
     * Check whether a player has passed this checkpoint
     */
    public boolean hasPassed(Player player) {
        return player.checkpointGoal >= this.id;
    }

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        if (hasPassed(player)) {
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


    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();
        jsonObject.addProperty("checkpointId", this.id);

        return jsonObject;
    }


    @Override
    public ISerializable deserialize(JsonElement element) {
        return super.deserialize(element);
    }
}
