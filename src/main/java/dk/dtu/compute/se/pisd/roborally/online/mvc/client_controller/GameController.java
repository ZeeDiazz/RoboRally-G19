package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import org.jetbrains.annotations.NotNull;

public class GameController {


    final public Board board;
    private static ExecuteCommands executeCommands;


    public GameController(@NotNull Board board) {
        this.board = board;
        executeCommands = new ExecuteCommands(board);
    }

    /**
     * Starts the programming phase. If randomCards is true, random cards will be generated for each player
     *
     * @param randomCards True if cards needs to be randomly generated
     */

    public void startProgrammingPhase(boolean randomCards) {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayerCount(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NUMBER_OF_REGISTERS; j++) {
                    CommandCardField field = player.getCardAtIndexFromProgramField(j);
                    if (randomCards) {
                        field.setCard(null);
                        field.setVisible(true);
                    }

                }
                for (int j = 0; j < Player.NUMBER_OF_CARDS; j++) {
                    CommandCardField field = player.getCardAtIndexFromCardField(j);
                    if (randomCards) {
                        field.setCard(generateRandomCommandCard());
                    }
                    field.setVisible(true);
                }
            }
        }
    }


    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);

        return new CommandCard(commands[random]);
    }


}


// Needs board
// public ExecuteCommands ()
