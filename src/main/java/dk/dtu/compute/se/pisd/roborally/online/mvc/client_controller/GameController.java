package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Command;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.ExecuteCommands;

public class GameController {


    private ExecuteCommands executeCommands;


    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        
        return new CommandCard(commands[random]);

    }


}


// Needs board
// public ExecuteCommands ()
