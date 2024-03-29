package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.old.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.old.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    private Board board;
    
    @BeforeEach
    void setUp() {
         board = MapMaker.makeDizzyHighway();
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
    void moveForward(){
      
        Player current = board.getCurrentPlayer();
        gameController.moveForward(current);

        Position position = new Position(0,1);
        Assertions.assertEquals(current.getSpace(),board.getSpace(position));
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
}