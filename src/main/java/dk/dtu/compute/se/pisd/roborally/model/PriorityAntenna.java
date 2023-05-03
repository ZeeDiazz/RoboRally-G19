package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class PriorityAntenna extends Space{

    public PriorityAntenna(Board board, int x, int y){
        super(board,x,y);

    }

    /**
     *
     * 
     * @author Felix723
     * @param players
     * @return   prioritylist, a list of player sorted  by priority
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
        int xAntenna = this.Position.X;
        int yAntenna = this.Position.Y;
        int xPlayer = player.getSpace().Position.X;
        int yPlayer = player.getSpace().Position.Y;
        return Math.abs(xPlayer-xAntenna) + Math.abs(yPlayer-yAntenna);
    }
    protected double getAngle(Player player){
        // (x1,y1) = priorityantenna
        // (x2,y2) = player

        int vx = this.Position.X;
        int vy = this.Position.Y;

        int ux = player.getSpace().Position.X;
        int uy = player.getSpace().Position.Y;
            if(uy == vy){
                uy++;
            }
        int xDifference = ux - vx;
        int yDifference = uy - vy;

        //double cos = (vx - ux)/(vy -uy);
        //return Math.acos(cos);
        return Math.atan2(yDifference,xDifference);
    }

}
