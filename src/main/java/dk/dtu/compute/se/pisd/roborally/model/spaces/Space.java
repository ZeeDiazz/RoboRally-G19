package dk.dtu.compute.se.pisd.roborally.model.spaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ISerializable;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic space, with nothing special going on.
 * @author Daniel Jensen
 */
public class Space extends Subject implements ISerializable {
    public final Position position;
    protected final ArrayList<Heading> walls;
    protected Player standingOn;

    /**
     * Creates a generic space, without anything special going on.
     * @param position the position of the space.
     * @param walls the walls of this space (can be empty for no walls).
     * @author Daniel Jensen
     */
    public Space(Position position, Heading... walls) {
        this.position = position;
        this.walls = new ArrayList<>(List.of(walls));
    }

    /**
     * Should be called when a player lands on this space.
     * This also includes when just passing over the space as part of a players move.
     * @param player the player who landed on this space.
     * @author Daniel Jensen
     */
    public void landedOn(Player player) {
        // do nothing
    }

    /**
     * Should be called when a player ends the register on this space.
     * This is mostly useful for the conveyor belts, gears, etc. as these have an effect on the player if they end the register while standing there.
     * @param player the player who ended the register on this space.
     * @param registerIndex the index of the register we are currently on.
     * @return the move the space wants the player to do, and null if no move at all.
     * @author Daniel Jensen
     */
    public Move endedRegisterOn(Player player, int registerIndex) {
        // do nothing
        return null;
    }

    /**
     * Whether this space has a wall in the given direction.
     * @param direction the direction to check for a wall.
     * @return true if there is a wall in the given direction, otherwise false
     * @author Daniel Jensen
     */
    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    /**
     * Method to determine if a player can enter from the given direction.
     * This will be a combination of the walls on the space, and whether the space can even be walked on.
     * @param from the direction trying to enter from. If a player moves north into this space, the "from" will be south, as it's from the space's perspective.
     * @return true if a player can enter this space from the given direction.
     * @author Daniel Jensen
     */
    public boolean canEnterFrom(Heading from) {
        return !hasWall(from);
    }

    /**
     * Method to determine if a player can exit via the given direction.
     * This will be a combination of the walls on the space, and whether the space can even be walked on.
     * @param going the direction going when standing on the space. If a player moves north away from this space, the "going" will also be north.
     * @return true if a player can exit this space via the given direction.
     * @author Daniel Jensen
     */
    public boolean canExitBy(Heading going) {
        return !hasWall(going);
    }

    /**
     * Add a wall to this space
     * @param direction The direction to place the wall
     * @author Daniel Jensen
     */
    public void addWall(Heading direction) {
        if (!hasWall(direction)) {
            walls.add(direction);
            changed();
        }
    }

    /**
     * Make a copy of this space, but place it on a new location.
     * @param newPosition the position of the copy.
     * @return the copied space with the new position.
     * @author Daniel Jensen
     */
    public Space copy(Position newPosition) {
        return new Space(newPosition, this.walls.toArray(new Heading[0]));
    }

    /**
     * Method to rotate this space to the left. This is useful when rotating whole boards.
     * @author Daniel Jensen
     */
    public void rotateLeft() {
        int wallCount = walls.size();
        for (int i = 0; i < wallCount; i++) {
            walls.add(Heading.turnLeft(walls.remove(0)));
        }
    }

    // Hack
    public void changed() {
        notifyChange();
    }

    // TODO refactor so these aren't necessary?

    /**
     * Set the player currently standing on this space to the given player.
     * @param player the player who should now be standing on this space.
     * @author Daniel Jensen
     */
    public void setPlayer(Player player) {
        this.standingOn = player;
        changed();
    }

    /**
     * Get the player currently standing on this space.
     * @return the player standing on this space, if no player is standing on the space, returns null.
     * @author Daniel Jensen
     */
    public Player getPlayer() {
        return standingOn;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("spaceType", this.getClass().getSimpleName());
        jsonObject.add("boardPosition", this.position.serialize());

        JsonArray jsonArrayWalls = new JsonArray();
        for (Heading wall : walls) {
            jsonArrayWalls.add(wall.toString());
        }
        jsonObject.add("walls", jsonArrayWalls);
        if (this.standingOn != null) {
            jsonObject.addProperty("playerOccupyingSpace", this.standingOn.getName());
        }

        return jsonObject;
    }

    /**
     * Deserializes the given JSON element into an instance of a class that implements ISerializable.
     *
     * @param element the JSON element to deserialize
     * @return an instance of a class that implements ISerializable
     */
    @Override
    public ISerializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        Position position = new Position(0, 0);
        position = (Position)position.deserialize(jsonObject.get("boardPosition"));

        ArrayList<Heading> wallsList = new ArrayList<>();
        for (JsonElement wallJson : jsonObject.get("walls").getAsJsonArray()) {
            wallsList.add(Heading.valueOf(wallJson.getAsString()));
        }
        Heading[] walls = wallsList.toArray(new Heading[0]);

        String type = jsonObject.get("spaceType").getAsString();
        Heading direction;
        switch (type) {
            case "Space":
                return new Space(position, walls);
            case "EnergySpace":
                return new EnergySpace(position, walls);
            case "GreenGearSpace":
                return new GreenGearSpace(position, walls);
            case "RedGearSpace":
                return new RedGearSpace(position, walls);
            case "PitSpace":
                return new PitSpace(position, walls);
            case "CheckPointSpace":
                int id = jsonObject.get("checkpointId").getAsInt();
                return new CheckPointSpace(position, id, walls);
            case "GreenConveyorSpace":
                direction = Heading.valueOf(jsonObject.get("heading").getAsString());
                return new GreenConveyorSpace(position, direction, walls);
            case "BlueConveyorSpace":
                direction = Heading.valueOf(jsonObject.get("heading").getAsString());
                return new BlueConveyorSpace(position, direction, walls);
        }
        return null;
    }
}
