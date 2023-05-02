package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Position;

public class RedGearSpace extends GearSpace {
    public RedGearSpace(Board board, Position position, Heading... walls) {
        super(board, position, walls);
    }

    public RedGearSpace(Board board, Position position) {
        super(board, position);
    }

    @Override
    protected void turnPlayer(Player player) {
        player.setHeading(Heading.turnLeft(player.getHeading()));
    }
}
