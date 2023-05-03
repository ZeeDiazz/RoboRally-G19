package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class BlueConveyorSpace extends ConveyorSpace {
    public BlueConveyorSpace(Position position, Heading direction, Heading... walls) {
        super(position, direction, 2, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        return new BlueConveyorSpace(newPosition, this.direction, this.walls.toArray(new Heading[0]));
    }
}
