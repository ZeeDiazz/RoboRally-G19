package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;


/**
 * Represent a priority antenna space
 *
 * @author Felix
 */
public class PriorityAntennaSpace extends Space {
    protected HeadingDirection direction;

    public PriorityAntennaSpace(Position position, HeadingDirection direction, HeadingDirection... walls) {
        super(position, walls);
        this.direction = direction;
    }

    @Override
    public boolean canEnterBy(HeadingDirection from) {
        return false;
    }

    @Override
    public boolean canExitBy(HeadingDirection going) {
        return false;
    }
}
