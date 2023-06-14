package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Move;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic space, with nothing special going on.
 *
 * @author Daniel Jensen
 */
public class Space extends Subject implements Serializable {
    public final Position position;
    protected final ArrayList<HeadingDirection> walls;
    protected Robot standingOn;

    /**
     * Creates a generic space, without anything special going on.
     *
     * @param position the position of the space.
     * @param walls    the walls of this space (can be empty for no walls).
     * @author Daniel Jensen
     */
    public Space(Position position, HeadingDirection... walls) {
        this.position = position;
        this.walls = new ArrayList<>(List.of(walls));
    }

    /**
     * Should be called when a robot lands on this space.
     * This also includes when just passing over the space as part of a robot's move.
     *
     * @param robot the robot who landed on this space.
     * @author Daniel Jensen
     */
    public void landedOn(Robot robot) {
        // do nothing
    }

    /**
     * Should be called when a robot ends the register on this space.
     * This is mostly useful for the conveyor belts, gears, etc. as these have an effect on the robot if they end the register while standing there.
     *
     * @param robot         the robot who ended the register on this space.
     * @param registerIndex the index of the register we are currently on.
     * @return the move the space wants the robot to do, and null if no move at all.
     * @author Daniel Jensen
     */
    public Move endedRegisterOn(Robot robot, int registerIndex) {
        // do nothing
        return null;
    }

    /**
     * Whether this space has a wall in the given headingDirection.
     *
     * @param headingDirection the headingDirection to check for a wall.
     * @return true if there is a wall in the given headingDirection, otherwise false
     * @author Daniel Jensen
     */
    public boolean hasWall(HeadingDirection headingDirection) {
        return walls.contains(headingDirection);
    }

    /**
     * Method to determine if a robot can enter from the given direction.
     * This will be a combination of the walls on the space, and whether the space can even be walked on.
     *
     * @param from the direction trying to enter from. If a robot moves north into this space, the "from" will be south, as it's from the space's perspective.
     * @return true if a robot can enter this space from the given direction.
     * @author Daniel Jensen
     */
    public boolean canEnterBy(HeadingDirection from) {
        return !hasWall(HeadingDirection.oppositeHeadingDirection(from));
    }

    /**
     * Method to determine if a robot can exit via the given direction.
     * This will be a combination of the walls on the space, and whether the space can even be walked on.
     *
     * @param going the direction going when standing on the space. If a robot moves north away from this space, the "going" will also be north.
     * @return true if a robot can exit this space via the given direction.
     * @author Daniel Jensen
     */
    public boolean canExitBy(HeadingDirection going) {
        return !hasWall(going);
    }

    /**
     * Add a wall to this space
     *
     * @param direction The direction to place the wall
     * @author Daniel Jensen
     */
    public void addWall(HeadingDirection direction) {
        if (!hasWall(direction)) {
            walls.add(direction);
            changed();
        }
    }

    /**
     * Make a copy of this space, but place it on a new location.
     *
     * @param newPosition the position of the copy.
     * @return the copied space with the new position.
     * @author Daniel Jensen
     */
    public Space copy(Position newPosition) {
        return new Space(newPosition, this.walls.toArray(new HeadingDirection[0]));
    }

    /**
     * Method to rotate this space to the left. This is useful when rotating whole boards.
     *
     * @author Daniel Jensen
     */
    public void rotateLeft() {
        int wallCount = walls.size();
        for (int i = 0; i < wallCount; i++) {
            walls.add(HeadingDirection.leftHeadingDirection(walls.remove(0)));
        }
    }

    public Position getPosition() {
        return position;
    }

    // Hack
    public void changed() {
        notifyChange();
    }

    // TODO refactor so these aren't necessary?

    /**
     * Set the robot currently standing on this space to the given robot.
     *
     * @param robot the robot who should now be standing on this space.
     * @author Daniel Jensen
     */
    public void setRobot(Robot robot) {
        this.standingOn = robot;
        changed();
    }

    /**
     * Get the robot currently standing on this space.
     *
     * @return the robot standing on this space, if no robot is standing on the space, returns null.
     * @author Daniel Jensen
     */
    public Robot getRobot() {
        return standingOn;
    }

    public void setRobotOnSpace(Robot robot) {
        this.standingOn = robot;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("spaceType", this.getClass().getSimpleName());

        jsonObject.add("boardPosition", this.position.serialize());

        JsonArray jsonArrayWalls = new JsonArray();
        for (HeadingDirection wall : walls) {
            jsonArrayWalls.add(wall.toString());
        }
        jsonObject.add("walls", jsonArrayWalls);


        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();


        Position position = new Position(0, 0);
        position = (Position) position.deserialize(jsonObject.get("boardPosition"));

        ArrayList<HeadingDirection> wallsList = new ArrayList<>();
        for (JsonElement wallJson : jsonObject.get("walls").getAsJsonArray()) {
            wallsList.add(HeadingDirection.valueOf(wallJson.getAsString()));
        }
        HeadingDirection[] walls = wallsList.toArray(new HeadingDirection[0]);

        String type = jsonObject.get("spaceType").getAsString();
        HeadingDirection direction;
        switch (type) {
            case "Space":
                return new Space(position, walls);
            case "EnergySpace":
                return new EnergySpace(position, walls);
            case "GreenGearSpace":
                direction = HeadingDirection.valueOf(jsonObject.get("headingDirection").getAsString());
                return new GreenGearSpace(position,direction, walls);
            case "RedGearSpace":
                direction = HeadingDirection.valueOf(jsonObject.get("headingDirection").getAsString());
                return new RedGearSpace(position,direction, walls);
            case "CheckPointSpace":
                int id = jsonObject.get("checkpointId").getAsInt();
                return new CheckPointSpace(position, id, walls);
            case "GreenConveyorSpace":
                direction = HeadingDirection.valueOf(jsonObject.get("headingDirection").getAsString());
                return new GreenConveyorSpace(position, direction, walls);
            case "BlueConveyorSpace":
                direction = HeadingDirection.valueOf(jsonObject.get("headingDirection").getAsString());
                return new BlueConveyorSpace(position, direction, walls);
            case "PriorityAntennaSpace":
                return new PriorityAntennaSpace(position, HeadingDirection.NORTH, walls);
        }

        // Shouldn't reach this
        return null;
    }

}
