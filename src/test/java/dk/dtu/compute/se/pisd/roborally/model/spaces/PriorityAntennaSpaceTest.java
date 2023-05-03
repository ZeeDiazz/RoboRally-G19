package dk.dtu.compute.se.pisd.roborally.model.spaces;


import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Position;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityAntennaTest {
    @Test
    void testPriorityAntennaWithNoTie(){

        PriorityAntennaSpace priorityAntenna = new PriorityAntennaSpace(new Position(0,0), Heading.NORTH);
        Player player1 = new Player(null,"purple","Felix");
        Player player2 = new Player(null,"blue","Daniel");
        Player player3 = new Player(null,"yellow","Zaid");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        player1.setSpace(new Space(new Position(0,4),Heading.SOUTH));
        player2.setSpace(new Space(new Position(0,2),Heading.SOUTH));
        player3.setSpace(new Space(new Position(0,1),Heading.SOUTH));
        List<Player> priorityPlayers = priorityAntenna.getPriority(players);

        Player priorityPlayer1 = priorityPlayers.get(0);
        Player priorityPlayer2 = priorityPlayers.get(1);
        Player priorityPlayer3 = priorityPlayers.get(2);
        Assertions.assertTrue(priorityPlayer1.equals(player3));
        Assertions.assertTrue(priorityPlayer2.equals(player2));
        Assertions.assertTrue(priorityPlayer3.equals(player1));


    }

    @Test
    void testPriorityAntennaWithTieAndPlayerBelowAntenna(){
        PriorityAntennaSpace priorityAntenna = new PriorityAntennaSpace(new Position(0,0), Heading.NORTH);
        Player player1 = new Player(null,"purple",  "Felix");
        Player player2 = new Player(null,"blue",  "Daniel");
        Player player3 = new Player(null, "yellow", "Zaid");
        Player player4 = new Player(null, "orange", "Dulle");
        Player player5 = new Player(null,  "grey",  "Zach");
        Player player6 = new Player(null, "red", "Emma");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        players.add(player5);
        players.add(player6);
        player1.setSpace(new Space(new Position(4,5)));
        player2.setSpace(new Space(new Position(0,3)));
        player3.setSpace(new Space(new Position(3,2)));
        player4.setSpace(new Space(new Position(5,2)));
        player5.setSpace(new Space(new Position(6,2)));
        player6.setSpace(new Space(new Position(7,2)));
        //  zaid, daniel, felix ,dulle, zach, emma

        List<Player> priorityPlayers = priorityAntenna.getPriority(players);
        Player priorityPlayer1 = priorityPlayers.get(0);
        Player priorityPlayer2 = priorityPlayers.get(1);
        Player priorityPlayer3 = priorityPlayers.get(2);
        Player priorityPlayer4 = priorityPlayers.get(3);
        Player priorityPlayer5 = priorityPlayers.get(4);
        Player priorityPlayer6 = priorityPlayers.get(5);


        Assertions.assertTrue(priorityPlayer1.equals(player3));
        Assertions.assertTrue(priorityPlayer2.equals(player2));
        Assertions.assertTrue(priorityPlayer3.equals(player4));
        Assertions.assertTrue(priorityPlayer4.equals(player1));
        Assertions.assertTrue(priorityPlayer5.equals(player5));
        Assertions.assertTrue(priorityPlayer6.equals(player6));
    }

    @Test
    void testPriorityAntennaWithTieWithoutPlayerBelowAntenna(){
        PriorityAntennaSpace priorityAntenna = new PriorityAntennaSpace(new Position(0,0), Heading.NORTH);
        Player player1 = new Player(null,"purple",  "Felix");
        Player player2 = new Player(null,"blue",  "Daniel");
        Player player3 = new Player(null, "yellow", "Zaid");
        Player player4 = new Player(null, "orange", "Dulle");
        Player player5 = new Player(null,  "grey",  "Zach");
        Player player6 = new Player(null, "red", "Emma");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        players.add(player5);
        players.add(player6);
        player1.setSpace(new Space(new Position(2,1)));
        player2.setSpace(new Space(new Position(0,3)));
        player3.setSpace(new Space(new Position(3,2)));
        player4.setSpace(new Space(new Position(5,2)));
        player5.setSpace(new Space(new Position(6,2)));
        player6.setSpace(new Space(new Position(7,2)));
        // felix, zaid, daniel, dulle, zach, emma

        List<Player> priorityPlayers = priorityAntenna.getPriority(players);
        Player priorityPlayer1 = priorityPlayers.get(0);
        Player priorityPlayer2 = priorityPlayers.get(1);
        Player priorityPlayer3 = priorityPlayers.get(2);
        Player priorityPlayer4 = priorityPlayers.get(3);
        Player priorityPlayer5 = priorityPlayers.get(4);
        Player priorityPlayer6 = priorityPlayers.get(5);


        Assertions.assertTrue(priorityPlayer1.equals(player1));
        Assertions.assertTrue(priorityPlayer2.equals(player3));
        Assertions.assertTrue(priorityPlayer3.equals(player2));
        Assertions.assertTrue(priorityPlayer4.equals(player4));
        Assertions.assertTrue(priorityPlayer5.equals(player5));
        Assertions.assertTrue(priorityPlayer6.equals(player6));
    }
}