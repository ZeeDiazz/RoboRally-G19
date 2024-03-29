package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;

/**
 * The base class for all the conveyors. This class has the shared functionality of all conveyors
 *
 * @author Daniel Jensen
 */
public abstract class ConveyorSpace extends Space implements Serializable {
    protected HeadingDirection direction;
    protected final int amount;

    /**
     * Creates a new conveyor with the given parameters. This is only a constructor used for easier construction in the specific Conveyors.
     *
     * @param position  the position of the space.
     * @param direction the direction this conveyor pushes.
     * @param amount    the amount this conveyor pushes.
     * @param walls     the walls of this space (can be empty for no walls).
     * @author Daniel Jensen
     */
    protected ConveyorSpace(Position position, HeadingDirection direction, int amount, HeadingDirection... walls) {
        super(position, walls);

        this.direction = direction;
        this.amount = amount;
    }


    /**
     * Get the direction this conveyor is facing.
     *
     * @return the direction this conveyor is facing.
     * @author Daniel Jensen
     */
    public HeadingDirection getDirection() {
        return direction;
    }

    /**
     * Should be called when a robot ends the register on this space.
     * This will generate the move the conveyor tries to impose on the player.
     *
     * @param robot         the robot who ended the register on this space.
     * @param registerIndex the index of the register we are currently on.
     * @return the move the conveyor wants this player to perform.
     * @author Daniel Jensen
     */
    @Override
    public Move endedRegisterOn(Robot robot, int registerIndex) {
        return new Move(this.position, this.direction, this.amount, robot);
    }

    @Override
    public void rotateLeft() {
        super.rotateLeft();
        this.direction = HeadingDirection.leftHeadingDirection(this.direction);
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();
        jsonObject.addProperty("headingDirection", this.direction.toString());

        return jsonObject;
    }
}
