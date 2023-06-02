package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;

import dk.dtu.compute.se.pisd.roborally.old.model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.old.model.Player;
import dk.dtu.compute.se.pisd.roborally.old.model.Position;

public class RedGearSpace extends GearSpace {
    /**
     * Represents a space on the board with a red gear that rotates players left when they end their turn on it.
     */
    public RedGearSpace(Position position, HeadingDirection... walls) {
        super(position, walls);
    }

    /**
     * Rotates the given player left when they end their turn on this space.
     *
     * @param player the player to rotate left
     */
    @Override
    protected void turnPlayer(Player player) {
        player.setHeading(HeadingDirection.leftHeadingDirection(player.getHeading()));
    }

    /**
     * Creates a new instance of this space with the given position and walls.
     *
     * @param newPosition the new position of the space
     * @return a new instance of this space with the given position and walls
     */
    @Override
    public Space copy(Position newPosition) {
        return new RedGearSpace(newPosition, this.walls.toArray(new HeadingDirection[0]));
    }
}
