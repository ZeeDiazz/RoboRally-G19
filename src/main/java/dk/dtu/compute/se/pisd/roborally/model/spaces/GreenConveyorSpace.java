package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class GreenConveyorSpace extends ConveyorSpace {
    public GreenConveyorSpace(Position position, Heading direction, Heading... walls) {
        super(position, direction, 1, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        return new GreenConveyorSpace(newPosition, direction, walls.toArray(new Heading[0]));
    }
}
