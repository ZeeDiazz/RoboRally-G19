package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class EnergySpace extends EmptySpace {
    public EnergySpace(Board board, Position position, Heading... walls) {
        super(board, position, walls);
    }

    public EnergySpace(Board board, Position position) {
        super(board, position);
    }
}
