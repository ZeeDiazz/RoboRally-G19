package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public abstract class PlayerType extends Subject {

    final public static int NUMBER_OF_REGISTERS = 5;
    final public static int NUMBER_OF_CARDS = 8;

    private String name;
    private int playerID;   // playerIndex
    public Robot robot;


    private int checkpointGoal = 0;
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

    public CommandCardField getCardAtIndexFromProgramField(int index) {
        return programField[index];
    }

    public CommandCardField getCardAtIndexFromCardField(int index) {
        return cards[index];
    }

}
