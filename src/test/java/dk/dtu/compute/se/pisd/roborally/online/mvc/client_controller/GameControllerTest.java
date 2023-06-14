package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController gameController;
    private Game game;
    private Board board;


    @BeforeEach
    void setUp() throws FileNotFoundException {
        board = MapMaker.makeJsonDizzyHighway();
        game = new LocalGame(board);
        gameController = new GameController(game);
    }


    @Test
    void startProgrammingPhase() {
        Player player = new LocalPlayer(game,"Red","Johnny boi");

        game.addPlayer(player);
        game.setCurrentPlayer(player);

        player.robot.setSpace(new Space(new Position(1,2)));

        game.setPhase(Phase.INITIALISATION);
        assertEquals(Phase.INITIALISATION, game.getPhase());

        gameController.startProgrammingPhase();

        assertEquals(Phase.PROGRAMMING, game.getPhase());
    }

    @Test
    void finishProgrammingPhase() {
        Player player = new LocalPlayer(game,"Red","Johnny boi");

        game.addPlayer(player);
        game.setCurrentPlayer(player);

        player.robot.setSpace(new Space(new Position(1,2)));

        game.setPhase(Phase.INITIALISATION);
        gameController.finishProgrammingPhase();

        assertEquals(Phase.ACTIVATION, game.getPhase());
    }

    @Test
    void spaceIsOccupied() {
        Player player1 = new LocalPlayer(game,"Red","Johnny boi");

        game.addPlayer(player1);
        game.setCurrentPlayer(player1);

        Space playerSpace = new Space(new Position(1,2));
        player1.robot.setSpace(playerSpace);

        boolean test =  gameController.spaceIsOccupied(playerSpace);

        assertTrue(test);
    }


    @Test
    void allCheckPointReached() {
        Player player1 = new LocalPlayer(game,"Red","Johnny boi");

        game.addPlayer(player1);
        game.setCurrentPlayer(player1);

        Space playerSpace = new Space(new Position(1,2));
        player1.robot.setSpace(playerSpace);

        boolean reachedAllCheckPoints = gameController.allCheckPointReached(player1.robot);
        assertFalse(reachedAllCheckPoints);
    }
}