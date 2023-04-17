package dk.dtu.compute.se.pisd.roborally.model;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * @author ZeeDaizz (Zaid)
 *
 */
public class Obstacle extends Space{
    private ObstacleType type;
    private Heading direction;

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
