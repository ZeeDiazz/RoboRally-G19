package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public abstract class GearSpace extends EmptySpace {

    public GearSpace(Board board, Position position, Heading... walls) {
        super(board, position, walls);
    }

    public GearSpace(Board board, Position position) {
        super(board, position);
    }

    protected abstract void turnPlayer(Player player);

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        turnPlayer(player);
        changed();
        return null;
    }
}
