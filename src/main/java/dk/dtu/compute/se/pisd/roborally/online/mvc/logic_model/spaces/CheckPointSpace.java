package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;

/**
 * This class represents a checkpointspace. It inherits from Space.Has a unique id.
 * @auther Daniel Jensen
 * @author Felix Schmidt
 */
public class CheckPointSpace extends Space implements Serializable {
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
        return robot.checkpointsReached >= this.id;
    }

    @Override
    public Move endedRegisterOn(Robot robot, int registerIndex) {
        if (hasPassed(robot)) {
            robot.checkpointsReached = this.id + 1;
            // player.setRebootSpace(this);
            changed();
        }
        return null;
    }

    @Override
    public Space copy(Position newPosition) {
        return new CheckPointSpace(newPosition, this.id, this.walls.toArray(new HeadingDirection[0]));
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();
        jsonObject.addProperty("checkpointId", this.id);

        return jsonObject;
    }
}
