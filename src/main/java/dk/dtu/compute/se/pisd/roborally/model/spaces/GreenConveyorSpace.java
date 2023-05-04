package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

/**
 * The class for a green conveyor. This is also known as a normal conveyor.
 * @author Daniel Jensen
 */
public class GreenConveyorSpace extends ConveyorSpace {
    /**
     * Creates a new green conveyor with the given parameters.
     * @param position the position of the space.
     * @param direction the direction this conveyor pushes.
     * @param walls the walls of this space (can be empty for no walls).
     * @author Daniel Jensen
     */
    public GreenConveyorSpace(Position position, Heading direction, Heading... walls) {
        super(position, direction, 1, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        return new GreenConveyorSpace(newPosition, direction, walls.toArray(new Heading[0]));
    }
}
