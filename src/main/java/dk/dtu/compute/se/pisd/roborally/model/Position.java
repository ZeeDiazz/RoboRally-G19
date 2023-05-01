package dk.dtu.compute.se.pisd.roborally.model;

public final class Position {
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

    public static Position add(Position p1, Position p2) {
        return new Position(p1.X + p2.X, p1.Y + p2.Y);
    }
}
