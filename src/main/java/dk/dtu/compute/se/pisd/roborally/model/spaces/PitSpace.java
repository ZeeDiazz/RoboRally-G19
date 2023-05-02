package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public class PitSpace extends EmptySpace {
    public PitSpace(Board board, Position position, Heading... walls) {
        super(board, position, walls);
    }

    public PitSpace(Board board, Position position) {
        super(board, position);
    }

    @Override
    public void landedOn(Player player) {
        // TODO check if they have the upgrade
        player.reboot();
        changed();
    }

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        player.reboot();
        changed();
        return null;
    }
}
