package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class BlueConveyorSpace extends ConveyorSpace {
    public BlueConveyorSpace(Board board, Position position, Heading direction, Heading... walls) {
        super(board, position, direction, 2, walls);
    }

    public BlueConveyorSpace(Board board, Position position, Heading direction) {
        this(board, position, direction, new Heading[0]);
    }
}
