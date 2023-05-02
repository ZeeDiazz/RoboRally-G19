package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public abstract class ConveyorSpace extends EmptySpace {
    public final Heading direction;

    public ConveyorSpace(Board board, Position position, Heading direction, Heading... walls) {
        super(board, position, walls);

        this.direction = direction;
    }

    public ConveyorSpace(Board board, Position position, Heading direction) {
        this(board, position, direction, new Heading[0]);
    }
}
