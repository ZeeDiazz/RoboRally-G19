package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class EnergySpace extends Space {
    public EnergySpace(Position position, Heading... walls) {
        super(position, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        // TODO when this space actually does something unique, make sure to fix this method
        return super.copy(newPosition);
    }
}
