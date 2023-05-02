package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ISerializable;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * @author ZeeDaizz (Zaid)
 * Obstacle is a kind of Space, which has a type and direction
 * Extends Space class
 */
public class Obstacle extends Space {
    private ObstacleType type;
    private Heading direction;

    /**
     * The constructor uses super, because the class extends Space.

     * @param type
     * @param direction
     */

    public Obstacle(Position position, ObstacleType type, Heading direction){
        super(position, false);

        this.type = type;
        this.direction = direction;
    }

    public ObstacleType getType() {
        return type;
    }

    public Heading getDirection() {
        return direction;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();

        jsonObject.addProperty("obstacleType", this.type.toString());
        jsonObject.addProperty("heading", this.direction.toString());

        return jsonObject;
        }

    @Override
    public ISerializable deserialize(JsonElement element) {
        return super.deserialize(element);
    }

    public Space copy(Position newPosition) {
        return new Obstacle(newPosition, this.type, this.direction);
    }

    @Override
    public void rotateLeft() {
        super.rotateLeft();
        this.direction = Heading.turnLeft(this.direction);

    }

    public void setType(ObstacleType type) {
        this.type = type;
    }

    public void setDirection(Heading direction) {
        this.direction = direction;
    }
}



