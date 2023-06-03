package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerType extends Subject {


    public PlayerType(Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.robot.setColor(color);
        //Player starts with 5 energy cube
        this.energyCubes = 5;
        programField = new CommandCardField[NUMBER_OF_REGISTERS];
        for (int i = 0; i < programField.length; i++) {
            programField[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NUMBER_OF_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }


    final public static int NUMBER_OF_REGISTERS = 5;
    final public static int NUMBER_OF_CARDS = 8;

    private String name;
    private int playerID;   // playerIndex
    public Robot robot;

    public static Board board;

    // In Robot class or this class?
    //private int checkpointReached = 0;
    private int energyCubes;

    private Command prevProgramming;

    private CommandCardField[] programField;
    private CommandCardField[] cards;


    //get/set robot

    public Command getPrevProgramming() {
        return prevProgramming;
    }

    /**
     * Set a programming as previous programming
     *
     * @param programming
     * @author ZeeDiazz (Zaid)
     */
    public void setPrevProgramming(Command programming) {
        prevProgramming = programming;
    }

    /**
     * Takes an amount of energy cube and adds to Players energy cubes
     * If amount is less or equal to 0 it does nothing
     *
     * @param amount of energy cube
     * @author Zeediazz (Zaid)
     */
    public void addEnergyCube(int amount) {
        if (amount > 0) {
            energyCubes += amount;
            notifyChange();
        }
    }

    /**
     * Takes an amount of energy cube and removes X amount of Players energy cubes
     * If amount is less or equal to 0 it does nothing
     *
     * @param amount
     * @author ZeeDiazz (Zaid)
     */
    public void removeEnergyCube(int amount) {
        if (amount > 0) {
            energyCubes -= amount;
            notifyChange();
        }
    }

    public CommandCardField getProgramField(int index) {
        return programField[index];
    }

    public CommandCardField getCardField(int index) {
        return cards[index];
    }

    public CommandCardField[] getCards() {
        return cards;
    }

    public CommandCardField[] getProgram() {
        return programField;
    }

    public String getName() {
        return name;
    }
}
