package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

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
     * @param board
     * @param x
     * @param y
     * @param type
     * @param direction
     */
    public Obstacle(Board board, int x, int y, ObstacleType type, Heading direction){
        super(board,x,y);
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
