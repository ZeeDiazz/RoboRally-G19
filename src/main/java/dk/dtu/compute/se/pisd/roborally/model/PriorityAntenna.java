package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class PriorityAntenna extends Space{

    public PriorityAntenna(Board board, int x, int y){
        super(board,x,y);

    }
    public List<Player> getPriority(List<Player> players){
        List<Player> tied = new ArrayList<>();
        List<Player> priority = new ArrayList<>();
        int previousPlayerDistance = -1;
        players.sort((a,b) -> (getDistanceTo(a) - getDistanceTo(b)));

        for(int i = 0; i < players.size(); i++){
            Player current = players.remove(0);
            if(getDistanceTo(current) != previousPlayerDistance){
                tied.sort((a, b) -> Double.compare(Math.atan(getSlope(a)), Math.atan(getSlope(b))));
                priority.addAll(tied);
            }
            previousPlayerDistance = getDistanceTo(current);
            tied.add(current);
        }
        tied.sort((a, b) -> Double.compare(Math.atan(getSlope(a)), Math.atan(getSlope(b))));
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
    protected double getSlope(Player player){
        // (x1,y1) = priorityantenna
        // (x2,y2) = player
        // s = sqrt((y2-y1)/(x2-x1)
        int xAntenna = this.Position.X;
        int yAntenna = this.Position.Y;
        int xPlayer = player.getSpace().Position.X;
        int yPlayer = player.getSpace().Position.Y;



        return Math.sqrt((yPlayer - yAntenna) / (double)(xPlayer - xAntenna));
    }


}
