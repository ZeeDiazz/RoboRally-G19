package dk.dtu.compute.se.pisd.roborally.online;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;

public interface Client {

    // private Game game;
    // private int clientId;


    // If the player prefer a game with a specific gameID. If it's possible, it should make the game with given gameID. 
    // If not, a valid gameID should be used to create the game
    Game createGame(int gameId);

    // If the player doesn't prefer to choose the gameID
    // Game will include the Player who makes the game
    Game createGame();

    // Returns a game where there is the newly made player
    Game joinGame(int gameId);

    boolean canStartGame();

    void finishedProgrammingPhase();

    boolean canStartActivationPhase();

    void sendStatusInfo();

    // (Ask if the game is finished)
    boolean gameIsFinished();

    // (To get the clientId of the player, from the server)
    boolean getPlayerId();

    // (save game)
    void saveGame();

    // (load game)
    void loadGame();

    // (to perform the move of the other players, when activationPhase can begin)
    void simulateActivationPhase();
}
