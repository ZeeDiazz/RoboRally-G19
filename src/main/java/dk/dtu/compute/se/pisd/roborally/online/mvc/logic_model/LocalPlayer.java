package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import org.jetbrains.annotations.NotNull;

/**
 * Primary constructor of a LocalPlayer
 * 
 * @author Zigalow & ZeeDiazz (Zaid)
 */

public class LocalPlayer extends Player {


    public LocalPlayer(Game game, String color, @NotNull String name) {
        super(game, color, name);
    }
}
