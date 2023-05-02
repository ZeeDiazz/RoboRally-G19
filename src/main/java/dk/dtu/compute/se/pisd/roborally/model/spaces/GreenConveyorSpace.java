package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class GreenConveyorSpace extends ConveyorSpace {
    public GreenConveyorSpace(Board board, Position position, Heading direction, Heading... walls) {
        super(board, position, direction, 1, walls);
    }

    public GreenConveyorSpace(Board board, Position position, Heading direction) {
        this(board, position, direction, new Heading[0]);
    }
}
