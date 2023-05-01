package dk.dtu.compute.se.pisd.roborally.model;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * @author ZeeDaizz (Zaid)
 * Obstacle is a kind of Space, which has a type and direction
 *Extends Space class
 */
public class Obstacle extends Space{
    private ObstacleType type;
    private Heading direction;

    /**
     * The constructor uses super, because the class extends Space.
     * @param x
     * @param y
     * @param type
     * @param direction
     */
    public Obstacle(Position position, ObstacleType type, Heading direction){
        super(position, false);
        this.type = type;
        this.direction = direction;
    }

    public ObstacleType getType() {
        return type;
    }

    public Heading getDirection() {
        return direction;
    }
}
