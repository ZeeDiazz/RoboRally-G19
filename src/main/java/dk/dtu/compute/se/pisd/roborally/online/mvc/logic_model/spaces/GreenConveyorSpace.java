package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.old.model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.old.model.Position;

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
    public GreenConveyorSpace(Position position, HeadingDirection direction, HeadingDirection... walls) {
        super(position, direction, 1, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        return new GreenConveyorSpace(newPosition, direction, walls.toArray(new HeadingDirection[0]));
    }
}
