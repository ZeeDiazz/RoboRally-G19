package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;


import javax.naming.Name;

public abstract class PlayerType extends Subject {

    private String name;
    private int playerID;   // playerIndex
    public Robot robot;
    private int checkpointGoal = 0;
    private int energyCube;

    private Command prevProgramming;

//    private CommandCardField[] program;
//    private CommandCardField[] cards;
//    private Command prevProgramming;
    
    //get/set robot
    
    public Command getPrevProgramming() {
        return prevProgramming;
    }

    /**
     * Set a programming as previous programming
     * @param programming
     * @author ZeeDiazz (Zaid)
     */
    public void setPrevProgramming(Command programming) {
        prevProgramming = programming;
    }

    /**
     * Takes an amount of energy cube and adds to Players energy cubes
     * If amount is less or equal to 0 it does nothing
     * @param amount of energy cube
     * @author Zeediazz (Zaid)
     */
    public void addEnergyCube(int amount){
        if(amount > 0) {
            energyCube += amount;
            notifyChange();
        }
    }

    /**
     * Takes an amount of energy cube and removes X amount of Players energy cubes
     * If amount is less or equal to 0 it does nothing
     * @param amount
     * @author ZeeDiazz (Zaid)
     */
    public void removeEnergyCube(int amount){
        if(amount > 0) {
            energyCube -= amount;
            notifyChange();
        }
    }
}
