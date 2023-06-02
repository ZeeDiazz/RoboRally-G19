package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.old.model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.old.model.Player;
import dk.dtu.compute.se.pisd.roborally.old.model.Position;

public class GreenGearSpace extends GearSpace {
    public GreenGearSpace(Position position, HeadingDirection... walls) {
        super(position, walls);
    }

    @Override
    protected void turnPlayer(Player player) {
        player.setHeading(HeadingDirection.rightDirectionOfDirection(player.getHeading()));
    }

    @Override
    public Space copy(Position newPosition) {
        return new GreenGearSpace(newPosition, this.walls.toArray(new HeadingDirection[0]));
    }
}
