package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Zigalow & ZeeDiazz (Zaid)
 */

public class Robot extends Subject implements Serializable {

    public int checkpointsReached = 0;

    private Space space;
    private String color;

    final public Player owner;

    public Position rebootPosition;
    private HeadingDirection headingDirection = HeadingDirection.SOUTH;

//    private CommandCardField[] program;
//    private CommandCardField[] cards;
//    private Command prevProgramming;

    //methods = damage, get pos, move


    public Robot(String color, Player player) {
        this.color = color;
        this.owner = player;
    }


    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setRobot(null);
            }
            if (space != null) {
                space.setRobot(this);
            }
            notifyChange();
        }
    }

    public HeadingDirection getHeadingDirection() {
        return headingDirection;
    }

    /**
     * Sets the absalute direction of the player.
     *
     * @param headingDirection the new direction (headingDirection) to be set.
     */
    public void setHeadingDirection(@NotNull HeadingDirection headingDirection) {
        if (headingDirection != this.headingDirection) {
            this.headingDirection = headingDirection;
            notifyChange();
            if (space != null) {
                space.changed();
            }
        }
    }

    public void setRebootPosition(Position position) {
        this.rebootPosition = position;
    }

    public Position getRebootPosition() {
        return rebootPosition;
    }


    /**
     * Reboot the player, setting their position to their reboot space (latest collected checkpoint)
     *
     * @author Daniel Jensen
     */
    public void reboot() {
        // TODO fix
        notifyChange();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.changed();
        }
    }

    public Player getOwner() {
        return owner;
    }


    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("checkpointsReached", this.checkpointsReached);
        jsonObject.addProperty("color", this.color);
        jsonObject.add("spacePosition", this.space.position.serialize());
        jsonObject.add("rebootSpace", this.rebootPosition.serialize());
        jsonObject.addProperty("headingDirection", this.headingDirection.toString());


        return jsonObject;

    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        Robot initialRobot = new Robot("red", null);

        initialRobot.checkpointsReached = jsonObject.getAsJsonPrimitive("checkpointsReached").getAsInt();
        initialRobot.color = jsonObject.getAsJsonPrimitive("color").getAsString();

        Space space = new Space(new Position(0, 0));
        Position position = new Position(0, 0);


        position = (Position) space.position.deserialize(jsonObject.get("spacePosition"));

        initialRobot.space = new Space(position);

        initialRobot.rebootPosition = (Position) position.deserialize(jsonObject.get("rebootSpace"));

        initialRobot.headingDirection = HeadingDirection.valueOf(jsonObject.getAsJsonPrimitive("headingDirection").getAsString());


        return initialRobot;
    }
}

