package dk.dtu.compute.se.pisd.roborally.model;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

public class Obstacle {
    private ObstacleType type;
    private Heading heading = SOUTH;

    public Obstacle(ObstacleType type){
        this.type = type;
    }

}
