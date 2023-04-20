package dk.dtu.compute.se.pisd.roborally.model;

import java.util.EnumSet;

public enum Wall {
    SOUTH(1),
    WEST(2),
    NORTH(4),
    EAST(8);

    private int flagValue;
    Wall(int flagValue) {
        this.flagValue = flagValue;
    }

    public int makeWalls(Wall... walls) {
        int flags = 0;
        for (Wall w : walls) {
            flags |= w.flagValue;
        }
        return flags;
    }

    public EnumSet<Wall> getWalls(int wallFlags) {
        EnumSet<Wall> walls = EnumSet.noneOf(Wall.class);
        for (Wall w : Wall.values()) {
            if ((w.flagValue & wallFlags) != 0) {
                walls.add(w);
            }
        }
        return walls;
    }
}
