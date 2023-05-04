package dk.dtu.compute.se.pisd.roborally.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player currentPlayer;
    @BeforeEach
    public void setUp() {
        Board board = new Board(8,8);
        currentPlayer = new Player(board, "blue", "Alice");
    }

    @Test
    void reboot() {
    }

    @Test
    void addEnergyCube() {
        //Check if the player has 5 energy cubes
        assertEquals(currentPlayer.getEnergyCube(),5);

        //Add two more energy cubes to current player
        currentPlayer.addEnergyCube(2);

        //Check if the energy cube is updated t 7
        assertEquals(currentPlayer.getEnergyCube(),7);

    }

    @Test
    void removeEnergyCube() {
        //Check if the player has 5 energy cubes
        assertEquals(currentPlayer.getEnergyCube(),5);

        //Remove three energy cube from the player
        currentPlayer.removeEnergyCube(3);

        //Check if the energy cube if a player is updated
        assertEquals(currentPlayer.getEnergyCube(),2);
    }
}