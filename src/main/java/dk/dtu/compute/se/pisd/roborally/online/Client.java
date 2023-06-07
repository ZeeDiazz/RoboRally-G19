package dk.dtu.compute.se.pisd.roborally.online;

import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;

public interface Client {

    Player joinGame(int gameId);

    boolean canStartGame(Game game);

    void finishedProgramming(Player player);

    boolean startActivationPhase();

    boolean finishedWithRegister();

}
