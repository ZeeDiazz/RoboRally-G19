package dk.dtu.compute.se.pisd.roborally.model;

import java.util.HashMap;

public final class MapMaker {
    public static Board makeCustomBoard(HashMap<Position, Space> specialSpaces, HashMap<Position, Heading[]> walls, int width, int height, String name) {
        Space[][] spaces = new Space[width][height];

        // Insert all the special spaces first
        for (Position position : specialSpaces.keySet()) {
            spaces[position.X][position.Y] = specialSpaces.get(position);
        }

        // Fill up the slots
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position currentPosition = new Position(x, y);
                if (specialSpaces.containsKey(currentPosition)) {
                    spaces[x][y] = specialSpaces.get(currentPosition);
                }
                else {
                    spaces[x][y] = new Space(x, y);
                }

                if (walls.containsKey(currentPosition)) {
                    for (Heading wallDirection : walls.get(currentPosition)) {
                        spaces[x][y].addWall(wallDirection);
                    }
                }
            }
        }

        return new Board(spaces, name);
    }

    public static Board makeDizzyHighway() {
        int width = 13;
        int height = 10;
        Space[][] spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space;
            }
        }

        Board highway = new Board(width, height, "Dizzy Highway");

        return highway;
    }
}
