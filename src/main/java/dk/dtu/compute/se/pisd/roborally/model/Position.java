package dk.dtu.compute.se.pisd.roborally.model;

public final class Position {
    public final int X;
    public final int Y;

    public Position(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public static Position move(Position position, Heading heading) {
        return move(position, heading, 1);
    }

    public static Position move(Position position, Heading heading, int amount) {
        int deltaX = 0;
        int deltaY = 0;
        switch (heading) {
            case SOUTH -> deltaY = 1;
            case WEST -> deltaX = -1;
            case NORTH -> deltaY = -1;
            case EAST -> deltaX = 1;
        }
        return new Position(position.X + deltaX * amount, position.Y + deltaY * amount);
    }
}
