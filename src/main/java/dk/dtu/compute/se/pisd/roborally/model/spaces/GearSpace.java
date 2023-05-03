package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public abstract class GearSpace extends Space {

    public GearSpace(Position position, Heading... walls) {
        super(position, walls);
    }

    protected abstract void turnPlayer(Player player);

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        turnPlayer(player);
        changed();
        return null;
    }
}
