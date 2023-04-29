package java.dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dk.dtu.compute.se.pisd.roborally.model.ObstacleType.BLUE_CONVEYOR_BELT;

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
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
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
    void testObstacleAction(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        //Player starts at 0,0 space
        gameController.moveCurrentPlayerToSpace( board.getSpace(0, 0));

        //Check if player starts at 0,0 space
        Assertions.assertEquals(current,board.getSpace(0, 0).getPlayer(),"Player " + current.getName() + " should be Space (0,0)!");

        //Space 0,1 is now a Blue Conveyor belt
        Space space = new Obstacle(board,0,1, BLUE_CONVEYOR_BELT, Heading.SOUTH);

        //Set players place at Blue Conveyor belt.
        current.setSpace(space);

        //Run the method obstacleAction
        gameController.obstacleAction(current);

        //Check if the player moved two spaces
        Assertions.assertEquals(current,board.getSpace(0, 3).getPlayer(),"Player " + current.getName() + " should be Space (0,3)!");
    }
}