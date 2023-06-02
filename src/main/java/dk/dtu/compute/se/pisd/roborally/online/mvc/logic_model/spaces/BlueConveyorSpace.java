package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;

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
    public BlueConveyorSpace(Position position, HeadingDirection direction, HeadingDirection... walls) {
        super(position, direction, 2, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        return new BlueConveyorSpace(newPosition, this.direction, this.walls.toArray(new HeadingDirection[0]));
    }
}
