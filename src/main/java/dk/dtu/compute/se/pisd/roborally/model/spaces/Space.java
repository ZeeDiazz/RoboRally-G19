package dk.dtu.compute.se.pisd.roborally.model.spaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ISerializable;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.List;

public class Space extends Subject implements ISerializable {
    public final Position position;
    protected final ArrayList<Heading> walls;
    protected Player standingOn;

    public Space(Position position, Heading... walls) {
        this.position = position;
        this.walls = new ArrayList<>(List.of(walls));
    }

    public void landedOn(Player player) {
        // do nothing
    }

    public Move endedRegisterOn(Player player, int registerIndex) {
        // do nothing
        return null;
    }

    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    public boolean canEnterFrom(Heading from) {
        return !hasWall(from);
    }

    public boolean canExitBy(Heading going) {
        return !hasWall(going);
    }

    /**
     * @param direction The direction to place the wall
     * @author Daniel Jensen
     * Add a wall to this space
     */
    public void addWall(Heading direction) {
        if (!hasWall(direction)) {
            walls.add(direction);
            changed();
        }
    }

    public Space copy(Position newPosition) {
        return new Space(newPosition, this.walls.toArray(new Heading[0]));
    }

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
    public void setPlayer(Player player) {
        this.standingOn = player;
        changed();
    }

    public boolean hasWalls(){
        return !this.walls.isEmpty();
    }
    
    public Player getPlayer() {
        return standingOn;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("spaceType",this.getClass().getSimpleName());
        
        


        jsonObject.add("boardPosition", this.position.serialize());
       

        if (!this.walls.isEmpty()) {
            JsonArray jsonArrayWalls = new JsonArray();
            for (Heading wall : walls) {
                jsonArrayWalls.add(wall.toString());
            }
            jsonObject.add("wall", jsonArrayWalls);
        }
        if (this.standingOn != null) {
            jsonObject.addProperty("playerOccupyingSpace", this.standingOn.getName());
        }

        return jsonObject;
    }

    @Override
    public ISerializable deserialize(JsonElement element) {
        return null;
    }
}
