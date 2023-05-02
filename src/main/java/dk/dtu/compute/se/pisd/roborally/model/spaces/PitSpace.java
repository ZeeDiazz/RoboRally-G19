package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public class PitSpace extends Space {
    public PitSpace(Position position, Heading... walls) {
        super(position, walls);
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

    @Override
    public Space copy(Position newPosition) {
        return new PitSpace(newPosition, walls.toArray(new Heading[0]));
    }
}
