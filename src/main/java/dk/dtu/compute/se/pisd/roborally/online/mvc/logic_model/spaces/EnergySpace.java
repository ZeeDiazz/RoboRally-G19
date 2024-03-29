package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;

public class EnergySpace extends Space {
    public EnergySpace(Position position, HeadingDirection... walls) {
        super(position, walls);
    }

    @Override
    public Space copy(Position newPosition) {
        // TODO when this space actually does something unique, make sure to fix this method
        return super.copy(newPosition);
    }
}
