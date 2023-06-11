package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Position;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;

import java.util.ArrayList;
import java.util.List;

public class PriorityAntennaSpace extends Space {
    protected HeadingDirection direction;

    public PriorityAntennaSpace(Position position, HeadingDirection direction ,HeadingDirection... walls){
        super(position, walls);
        this.direction = direction;
    }

    /**
     * This method is for creating and getting a list
     * of robots sorted by priority based roborrally rules
     * @author Felix723
     * @param robots
     * @return prioritylist, a list of player sorted  by priority
     */
    public List<Robot> getPriority(List<Robot> robots){
        List<Robot> tied = new ArrayList<>();
        List<Robot> priority = new ArrayList<>();
        int previousPlayerDistance = -1;
        int robotsSize = robots.size();
        robots.sort((a,b) -> (getDistanceTo(a) - getDistanceTo(b)));

        for(int i = 0; i < robotsSize; i++){
            Robot current = robots.remove(0);

            if(getDistanceTo(current) != previousPlayerDistance){
                tied.sort((a, b) -> Double.compare(getAngle(a),getAngle(b)));
                priority.addAll(tied);
                tied.clear();
            }
            previousPlayerDistance = getDistanceTo(current);
            tied.add(current);
        }
        tied.sort((a, b) -> Double.compare(getAngle(a),getAngle(b)));
        priority.addAll(tied);
        return priority;

    }
    /**
     * Returns the Manhattan distance between this antenna and the given robot's space.
     *
     * @param robot the robot whose distance to this antenna is to be calculated
     * @return the Manhattan distance between this antenna and the robot's space
     */
    protected int getDistanceTo(Robot robot){
        int xAntenna = this.position.X;
        int yAntenna = this.position.Y;
        int xPlayer = robot.getSpace().position.X;
        int yPlayer = robot.getSpace().position.Y;
        return Math.abs(xPlayer-xAntenna) + Math.abs(yPlayer-yAntenna);
    }
    /**
     * Returns the angle between this antenna and the given robot's space in radians.
     *
     * @param robot the robot whose angle to this antenna is to be calculated
     * @return the angle between this antenna and the robot's space in radians
     */
    protected double getAngle(Robot robot){
        // (x1,y1) = priorityantenna
        // (x2,y2) = robot

        int xAntenna = this.position.X;
        int yAntenna = this.position.Y;

        int xPlayer = robot.getSpace().position.X;
        int yPlayer = robot.getSpace().position.Y;
        if(yPlayer == yAntenna){
            yPlayer++;
        }
        int xDifference = xPlayer - xAntenna;
        int yDifference = yPlayer - yAntenna;

        //double cos = (xAntenna - xPlayer)/(yAntenna -yPlayer);
        //return Math.acos(cos);
        return Math.atan2(yDifference,xDifference);
    }

}
