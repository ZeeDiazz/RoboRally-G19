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
    void moveForward(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.moveForward(current);

        Position position = new Position(0,1);
        Assertions.assertEquals(current.getSpace().position,board.getSpace(position));
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