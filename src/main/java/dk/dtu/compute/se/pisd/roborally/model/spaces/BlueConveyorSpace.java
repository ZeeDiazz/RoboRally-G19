package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Position;

/**
 * The class for a blue conveyor. This is also known as a fast conveyor.
 * @author Daniel Jensen
 */
public class BlueConveyorSpace extends ConveyorSpace {
    /**
     * Creates a new blue conveyor with the given parameters.
     * @param position the position of the space.
     * @param direction the direction this conveyor pushes.
     * @param walls the walls of this space (can be empty for no walls).
     * @author Daniel Jensen
     */
    public BlueConveyorSpace(Position position, Heading direction, Heading... walls) {
        super(position, direction, 2, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        return new BlueConveyorSpace(newPosition, this.direction, this.walls.toArray(new Heading[0]));
    }
}
