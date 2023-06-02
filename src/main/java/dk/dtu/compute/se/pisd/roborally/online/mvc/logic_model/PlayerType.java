package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import javax.naming.Name;

public abstract class PlayerType {

    private String name;
    private int playerID;   // playerIndex

    public PlayerType(String name, int playerID) {
        this.name = name;
        this.playerID = playerID;

        //save the players card both hand and register
    }

    //get/set robot



}
