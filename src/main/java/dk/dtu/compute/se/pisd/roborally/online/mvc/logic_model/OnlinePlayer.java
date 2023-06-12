package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import org.jetbrains.annotations.NotNull;

public class OnlinePlayer extends Player {

    public OnlinePlayer(String color, @NotNull String name) {
        super(color, name);
    }


    /**
     * Default constructor of OnlinePlayer, so that an OnlinePlayer can be made without any parameters
     * @author Zigalow
     */
    public OnlinePlayer() {
        
    }
}
