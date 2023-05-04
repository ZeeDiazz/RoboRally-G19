package dk.dtu.compute.se.pisd.roborally.model.spaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.model.*;

public abstract class ConveyorSpace extends Space {
    protected Heading direction;
    protected final int amount;

    protected ConveyorSpace(Position position, Heading direction, int amount, Heading... walls) {
        super(position, walls);

        this.direction = direction;
        this.amount = amount;
    }

    public Heading getDirection() {
        return direction;
    }

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        return new Move(this.position, this.direction, this.amount, player);
    }

    @Override
    public void rotateLeft() {
        super.rotateLeft();
        this.direction = Heading.turnLeft(this.direction);
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();
        jsonObject.addProperty("heading", this.direction.toString());

        return jsonObject;
    }
    
}
