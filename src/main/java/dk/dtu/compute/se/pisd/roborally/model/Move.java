package dk.dtu.compute.se.pisd.roborally.model;

public final class Move {
    public final Position Start;
    public final Position End;

    public Move(Position start, Position end) {
        this.Start = start;
        this.End = end;
    }
}
