package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class PriorityAntenna extends Space{
    List<Player> closestPlayers = new ArrayList<>();

    public PriorityAntenna(Board board, int x, int y){
        super(board,x,y);
    }

    /**
     * this method set the priority player from a list of players
     * @param players
     * @param priorityAntenna
     */
    public void  setPriorityPlayer(List<Player> players, PriorityAntenna priorityAntenna) {
        Player closestPlayer = null;
        int closestDistance = Integer.MAX_VALUE;
        double smallestAngle = Double.MAX_VALUE;
        List<Player> closestPlayers = new ArrayList<>();
        for (Player player: players){
            int checkingPlayerDistance = player.getManhattanDistanceToAntenna(player, priorityAntenna);
            if(checkingPlayerDistance < closestDistance){
                closestPlayer = player;
                closestDistance = checkingPlayerDistance;
            }
        }


        /*if(!closestPlayers.isEmpty()){
            for(Player player : closestPlayers){
                double currentPlayerSlope = player.getSlope(player,priorityAntenna);
                double checkingPlayersAngle = Math.atan(currentPlayerSlope);
                if(checkingPlayersAngle < 0){
                    checkingPlayersAngle += Math.PI;
                }
                if(checkingPlayersAngle > smallestAngle){
                    closestPlayer = player;
                    smallestAngle = checkingPlayersAngle;
                }

            }

        }*/
        board.setCurrentPlayer(closestPlayer);
    }

}
