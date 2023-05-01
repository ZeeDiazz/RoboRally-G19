package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class PriorityAntenna extends Space{

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
        double closestDistance = 0.0;
        double smallestSlope = 0.0;
        for (Player player: players){
            double checkingPlayerDistance = player.getDistanceToAntenna(player, priorityAntenna);
            if(checkingPlayerDistance < closestDistance){
                closestPlayer = player;
                closestDistance = checkingPlayerDistance;
            }
        }

        List<Player> closestPlayers = new ArrayList<>();
        for(Player player : players){
            double currentPlayersDistance = player.getDistanceToAntenna(player, priorityAntenna);
            if(closestDistance == currentPlayersDistance){
                closestPlayers.add(player);
            }
        }

        if(closestPlayers != null){
            for(Player player : closestPlayers){
                double currentPlayerSlope = player.getSlopeBetweenAntennaAndPlayer(player,priorityAntenna);
                if(currentPlayerSlope < smallestSlope){
                    closestPlayer = player;
                    smallestSlope = currentPlayerSlope;
                }

            }

        }
        board.setCurrentPlayer(closestPlayer);
    }

}
