package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import dk.dtu.compute.se.pisd.roborally.old.model.Player;
import dk.dtu.compute.se.pisd.roborally.old.model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;

public class Robot extends Subject {

    final public static int NUMBER_OF_REGISTERS = 5;
    final public static int NUMBER_OF_CARDS = 8;
    public int checkpointReached = 0;
    private int energyCubes; //isn't it the player that has energy cubes?

    private Space space;
    private String color;


//  private Position rebootPosition;
//  private Heading heading = Heading.SOUTH;

//    private CommandCardField[] program;
//    private CommandCardField[] cards;
//    private Command prevProgramming;

    //methods = damage, get pos, move
}
