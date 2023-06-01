package dk.dtu.compute.se.pisd.roborally.old.model.spaces;

import dk.dtu.compute.se.pisd.roborally.old.model.Heading;
import dk.dtu.compute.se.pisd.roborally.old.model.Player;
import dk.dtu.compute.se.pisd.roborally.old.model.Position;

public class GreenGearSpace extends GearSpace {
    public GreenGearSpace(Position position, Heading... walls) {
        super(position, walls);
    }

    @Override
    protected void turnPlayer(Player player) {
        player.setHeading(Heading.turnRight(player.getHeading()));
    }

    @Override
    public Space copy(Position newPosition) {
        return new GreenGearSpace(newPosition, this.walls.toArray(new Heading[0]));
    }
}
