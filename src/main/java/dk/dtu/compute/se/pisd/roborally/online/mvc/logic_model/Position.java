package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.old.fileaccess.ISerializable;

public final class Position implements ISerializable {
    public final int X;
    public final int Y;

    public Position(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Position otherPos) {
            return this.X == otherPos.X && this.Y == otherPos.Y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.X) * 3 + Integer.hashCode(this.Y) * 7;
    }

    public static Position move(Position position, HeadingDirection headingDirection) {
        return move(position, headingDirection, 1);
    }

    public static Position move(Position position, HeadingDirection headingDirection, int amount) {
        int deltaX = 0;
        int deltaY = 0;
        switch (headingDirection) {
            case SOUTH -> deltaY = 1;
            case WEST -> deltaX = -1;
            case NORTH -> deltaY = -1;
            case EAST -> deltaX = 1;
        }
        return new Position(position.X + deltaX * amount, position.Y + deltaY * amount);
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("x",this.X);
        jsonObject.addProperty("y",this.Y);

        return jsonObject;
    }

    @Override
    public ISerializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();

        return new Position(x, y);
    }

    public static Position add(Position p1, Position p2) {
        return new Position(p1.X + p2.X, p1.Y + p2.Y);
    }
}
