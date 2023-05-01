package dk.dtu.compute.se.pisd.roborally.model;

import java.util.HashMap;

import static dk.dtu.compute.se.pisd.roborally.model.ObstacleType.*;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;

public final class MapMaker {
    public static Board makeCustomBoard(HashMap<Position, Space> specialSpaces, HashMap<Position, Heading[]> walls, Position[] spawnPoints, int width, int height, String name) {
        Space[][] spaces = new Space[width][height];

        // Fill up the slots
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space;
                Position currentPosition = new Position(x, y);
                if (specialSpaces.containsKey(currentPosition)) {
                    space = specialSpaces.get(currentPosition);
                }
                else {
                    space = new Space(currentPosition);
                }
                spaces[x][y] = space;

                if (walls.containsKey(currentPosition)) {
                    for (Heading wallDirection : walls.get(currentPosition)) {
                        space.addWall(wallDirection);
                    }
                }
            }
        }

        return new Board(spaces, name);
    }

    public static Board makeDizzyHighway() {
        HashMap<Position, Space> special = new HashMap<>();
        // Making special spaces
        // The two green conveyors
        special.put(new Position(2, 0), new Obstacle(2, 0, GREEN_CONVEYOR_BELT, EAST));
        special.put(new Position(2, 9), new Obstacle(2, 0, GREEN_CONVEYOR_BELT, EAST));

        // The blue conveyor ring
        for (int y = 0; y < 8; y++) {
            special.put(new Position(4, y), new Obstacle(4, y, BLUE_CONVEYOR_BELT, SOUTH));
        }
        special.put(new Position(5, 0), new Obstacle(5, 0, BLUE_CONVEYOR_BELT, SOUTH));
        for (int x = 5; x < 13; x++) {
            special.put(new Position(x, 1), new Obstacle(x, 1, BLUE_CONVEYOR_BELT, WEST));
        }
        special.put(new Position(12, 2), new Obstacle(12, 2, BLUE_CONVEYOR_BELT, SOUTH));
        for (int y = 2; y < 10; y++) {
            special.put(new Position(11, y), new Obstacle(11, y, BLUE_CONVEYOR_BELT, NORTH));
        }
        special.put(new Position(10, 9), new Obstacle(10, 9, BLUE_CONVEYOR_BELT, NORTH));
        for (int x = 3; x < 11; x++) {
            special.put(new Position(x, 8), new Obstacle(x, 8, BLUE_CONVEYOR_BELT, EAST));
        }
        special.put(new Position(3, 7), new Obstacle(3, 7, BLUE_CONVEYOR_BELT, SOUTH));

        special.put(new Position(12, 3), new CheckPoint(12, 3, 0));

        HashMap<Position, Heading[]> walls = new HashMap<>();
        walls.put(new Position(1, 2), new Heading[] {NORTH});
        walls.put(new Position(1, 7), new Heading[] {SOUTH});
        walls.put(new Position(2, 4), new Heading[] {EAST});
        walls.put(new Position(2, 5), new Heading[] {EAST});
        walls.put(new Position(6, 3), new Heading[] {NORTH});
        walls.put(new Position(6, 6), new Heading[] {WEST});
        walls.put(new Position(9, 3), new Heading[] {EAST});
        walls.put(new Position(9, 6), new Heading[] {SOUTH});

        Position[] spawnPoints = new Position[] {new Position(1, 1), new Position(0, 3), new Position(1, 4), new Position(1, 5), new Position(0, 6), new Position(1, 8)};
        return makeCustomBoard(special, walls, spawnPoints, 13, 10, "Dizzy Highway");
    }

    public static Board test() {
        int width = 8;
        int height = 8;
        Space[][] spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {

            for (int y = 0; y < height; y++) {
                Space space;

                //ZeeDiazz (Zaid) {
                if (x == 0 && y == 1 || x == 2 && y == 3) {
                    space = new Obstacle(x, y, BLUE_CONVEYOR_BELT, SOUTH);
                } else if (x == 1 && y == 5) {
                    space = new Obstacle(x, y, GREEN_CONVEYOR_BELT, NORTH);
                }

                //   }
                else if (x == 3 && y == 4) {
                    space = new CheckPoint(x, y, 0);
                } else if (x == 6 && y == 2) {
                    space = new CheckPoint(x, y, 1);
                } else {
                    space = new Space(x, y);
                }


                if (x == 1 && y == 1) {
                    space.addWall(Heading.SOUTH);
                }

                spaces[x][y] = space;
            }
        }
        return new Board(spaces, "default");
    }
}
