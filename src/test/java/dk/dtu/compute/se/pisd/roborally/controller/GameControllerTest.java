package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.spaces.BlueConveyorSpace;
import dk.dtu.compute.se.pisd.roborally.model.spaces.PriorityAntennaSpace;
import dk.dtu.compute.se.pisd.roborally.model.spaces.Space;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        /*Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        //gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");*/
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnAround() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.turnAround(current);

        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player 0 should be heading North!");
    }
    @Test
    void backUp() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
    }

    @Test
    void testObstacleAction(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        //Player starts at 0,0 space
       // gameController.moveCurrentPlayerToSpace(board.getSpace(0, 0));

        //Check if player starts at 0,0 space
        //Assertions.assertEquals(current,board.getSpace(0, 0).getPlayer(),"Player " + current.getName() + " should be Space (0,0)!");

        //Space 0,1 is now a Blue Conveyor belt
        //Space space = new BlueConveyorSpace(new Position(0, 1), Heading.SOUTH);

        //Set players place at Blue Conveyor belt.
        //current.setSpace(space);

        //Run the method obstacleAction
        //gameController.obstacleAction();

        //Check if the player moved two spaces
        //Assertions.assertEquals(current,board.getSpace(0, 3).getPlayer(),"Player " + current.getName() + " should be Space (0,3)!");
    }
    @Test
    void testPriorityAntennaSpace(){
        Board board = new Board(0,0);
        PriorityAntennaSpace antenna = new PriorityAntennaSpace(new Position(7,7));
        Player player1 = new Player(board,"purple",  "Felix");
        Player player2 = new Player(board,"blue",  "Daniel");
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        player1.setSpace(new Space(new Position(4, 4)));
        player2.setSpace(new Space(new Position(7, 6)));

        List<Player> priotityPlayers = antenna.getPriority(players);
        Player priorityPlayer = priotityPlayers.get(0);
        Assertions.assertEquals(priorityPlayer, player2);
    }
    @Test
    void testPriorityAntennaSpaceWithTie(){
        Board board = new Board(0,0);
        PriorityAntennaSpace  antenna = new PriorityAntennaSpace(new Position(2, 3));
        Player player1 = new Player(board,"purple",  "Felix");
        Player player2 = new Player(board,"blue",  "Daniel");
        Player player3 = new Player(board, "yellow", "Zaid");
        Player player4 = new Player(board, "orange", "Dulle");
        Player player5 = new Player(board,  "grey",  "Zach");
        Player player6 = new Player(board, "red", "Emma");
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

        List<Player> priorityPlayers = antenna.getPriority(players);
        Player priorityPlayer1 = priorityPlayers.get(0);
        Player priorityPlayer2 = priorityPlayers.get(1);
        Player priorityPlayer3 = priorityPlayers.get(2);
        Player priorityPlayer4 = priorityPlayers.get(3);
        Player priorityPlayer5 = priorityPlayers.get(4);
        Player priorityPlayer6 = priorityPlayers.get(5);


        Assertions.assertEquals(priorityPlayer1, player3);
        Assertions.assertEquals(priorityPlayer2, player2);
        Assertions.assertEquals(priorityPlayer3, player4);
        Assertions.assertEquals(priorityPlayer4, player1);
        Assertions.assertEquals(priorityPlayer5, player5);
        Assertions.assertEquals(priorityPlayer6, player6);
    }

    void testPriorityAntennaSpaceWithTieSecond(){
        Board board = new Board(0,0);
        PriorityAntennaSpace antenna = new PriorityAntennaSpace(new Position(7,7));
        Player player1 = new Player(board,"purple",  "Felix");
        Player player2 = new Player(board,"blue",  "Daniel");
        Player player3 = new Player(board, "yellow", "Zaid");
        Player player4 = new Player(board, "orange", "Dulle");
        Player player5 = new Player(board,  "grey",  "Zach");
        Player player6 = new Player(board, "red", "Emma");
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

        List<Player> priorityPlayers = antenna.getPriority(players);
        Player priorityPlayer = priorityPlayers.get(0);
        Assertions.assertEquals(priorityPlayer, player1);
    }

}