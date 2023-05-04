package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Position;

import java.util.ArrayList;
import java.util.List;

public class PriorityAntennaSpace extends Space{

    public PriorityAntennaSpace(Position position, Heading... walls){
        super(position, walls);
    }

    /**
     * This method is for creating and getting a list
     * of players sorted by priority based roborrally rules
     * @author Felix723
     * @param players
     * @return prioritylist, a list of player sorted  by priority
     */
    public List<Player> getPriority(List<Player> players){
        List<Player> tied = new ArrayList<>();
        List<Player> priority = new ArrayList<>();
        int previousPlayerDistance = -1;
        int playersSize = players.size();
        players.sort((a,b) -> (getDistanceTo(a) - getDistanceTo(b)));

        for(int i = 0; i < playersSize; i++){
            Player current = players.remove(0);

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

    protected int getDistanceTo(Player player){
        int xAntenna = this.position.X;
        int yAntenna = this.position.Y;
        int xPlayer = player.getSpace().position.X;
        int yPlayer = player.getSpace().position.Y;
        return Math.abs(xPlayer-xAntenna) + Math.abs(yPlayer-yAntenna);
    }
    protected double getAngle(Player player){
        // (x1,y1) = priorityantenna
        // (x2,y2) = player

        int xAntenna = this.position.X;
        int yAntenna = this.position.Y;

        int xPlayer = player.getSpace().position.X;
        int yPlayer = player.getSpace().position.Y;
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
