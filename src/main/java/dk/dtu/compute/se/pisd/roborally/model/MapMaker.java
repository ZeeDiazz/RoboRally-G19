package dk.dtu.compute.se.pisd.roborally.model;

import java.util.HashMap;

import static dk.dtu.compute.se.pisd.roborally.model.ObstacleType.*;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;

public final class MapMaker {
    public static Board makeCustomBoard(HashMap<Position, Space> specialSpaces, HashMap<Position, Heading[]> walls, int width, int height, String name) {
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
                    space = new Space(currentPosition, false);
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

    public static Board makeStartA() {
        HashMap<Position, Space> spaces = new HashMap<>();
        // Making special spaces
        // The spawn points
        addSpace(spaces, new Space(new Position(1, 1), true));
        addSpace(spaces, new Space(new Position(3, 2), true));
        addSpace(spaces, new Space(new Position(4, 1), true));
        addSpace(spaces, new Space(new Position(5, 1), true));
        addSpace(spaces, new Space(new Position(6, 2), true));
        addSpace(spaces, new Space(new Position(8, 1), true));

        // The two green conveyors
        addSpace(spaces, new Obstacle(new Position(0, 0), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(9, 0), GREEN_CONVEYOR_BELT, NORTH));

        HashMap<Position, Heading[]> walls = new HashMap<>();
        walls.put(new Position(2, 1), new Heading[] {WEST});
        walls.put(new Position(4, 0), new Heading[] {NORTH});
        walls.put(new Position(5, 0), new Heading[] {NORTH});
        walls.put(new Position(7, 1), new Heading[] {EAST});

        return makeCustomBoard(spaces, walls, 10, 3, "Start A");
    }

    public static Board make5A() {
        HashMap<Position, Space> spaces = new HashMap<>();
        // Making special spaces
        Position[] startingAt = new Position[] {new Position(1, 0), new Position(0, 8), new Position(9, 1), new Position(8, 9)};
        Heading[] startHeading = new Heading[] {SOUTH, EAST, WEST, NORTH};
        for (int i = 0; i < 4; i++) {
            Position position = startingAt[i];
            Heading direction = startHeading[i];
            addSpace(spaces, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
            position = Position.move(position, direction);
            addSpace(spaces, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
            position = Position.move(position, direction);
            direction = Heading.turnRight(direction);
            addSpace(spaces, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
            position = Position.move(position, direction);
            addSpace(spaces, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
        }

        addSpace(spaces, new Obstacle(new Position(5, 1), GREEN_CONVEYOR_BELT, WEST));
        addSpace(spaces, new Obstacle(new Position(4, 1), GREEN_CONVEYOR_BELT, WEST));
        addSpace(spaces, new Obstacle(new Position(3, 1), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(3, 2), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(3, 3), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(2, 3), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(2, 4), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(1, 4), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(1, 5), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(1, 6), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(2, 6), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(3, 6), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(4, 6), GREEN_CONVEYOR_BELT, WEST));
        addSpace(spaces, new Obstacle(new Position(4, 7), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(4, 8), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(5, 8), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(6, 8), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(6, 7), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(6, 6), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(7, 6), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(7, 5), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(spaces, new Obstacle(new Position(8, 5), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(8, 4), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(spaces, new Obstacle(new Position(8, 3), GREEN_CONVEYOR_BELT, WEST));
        addSpace(spaces, new Obstacle(new Position(7, 3), GREEN_CONVEYOR_BELT, WEST));
        addSpace(spaces, new Obstacle(new Position(6, 3), GREEN_CONVEYOR_BELT, WEST));
        addSpace(spaces, new Obstacle(new Position(5, 3), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(5, 2), GREEN_CONVEYOR_BELT, NORTH));

        HashMap<Position, Heading[]> walls = new HashMap<>();

        return makeCustomBoard(spaces, walls, 10, 10, "5A");
    }

    public static Board make5B() {
        HashMap<Position, Space> spaces = new HashMap<>();

        // Making special spaces
        // The blue conveyor ring
        for (int y = 0; y < 8; y++) {
            addSpace(spaces, new Obstacle(new Position(1, y), BLUE_CONVEYOR_BELT, SOUTH));
        }
        addSpace(spaces, new Obstacle(new Position(2, 0), BLUE_CONVEYOR_BELT, SOUTH));
        for (int x = 2; x < 10; x++) {
            addSpace(spaces, new Obstacle(new Position(x, 1), BLUE_CONVEYOR_BELT, WEST));
        }
        addSpace(spaces, new Obstacle(new Position(9, 2), BLUE_CONVEYOR_BELT, WEST));
        for (int y = 2; y < 10; y++) {
            addSpace(spaces, new Obstacle(new Position(8, y), BLUE_CONVEYOR_BELT, NORTH));
        }
        addSpace(spaces, new Obstacle(new Position(2, 0), BLUE_CONVEYOR_BELT, NORTH));
        for (int x = 0; x < 8; x++) {
            addSpace(spaces, new Obstacle(new Position(x, 8), BLUE_CONVEYOR_BELT, EAST));
        }
        addSpace(spaces, new Obstacle(new Position(0, 7), BLUE_CONVEYOR_BELT, EAST));

        HashMap<Position, Heading[]> walls = new HashMap<>();
        walls.put(new Position(3, 3), new Heading[] {NORTH});
        walls.put(new Position(3, 6), new Heading[] {WEST});
        walls.put(new Position(6, 3), new Heading[] {EAST});
        walls.put(new Position(6, 6), new Heading[] {SOUTH});

        return makeCustomBoard(spaces, walls, 10, 10, "5B");
    }

    public static Board makeDizzyHighway() {
        Board startA = makeStartA();
        Board fiveB = make5B();
        Board dizzyHighway = Board.add(Board.rotateRight(startA), fiveB, new Position(3, 0), "Dizzy Highway");
        dizzyHighway.addCheckpoint(new Position(12, 3));
        return dizzyHighway;
    }

    public static Board makeRiskyCrossing() {
        Board startA = makeStartA();
        Board fiveA = make5A();
        Board riskyCrossing = Board.add(Board.rotateRight(startA), fiveA, new Position(3, 0), "Risky Crossing");
        riskyCrossing.addCheckpoint(new Position(8, 7));
        riskyCrossing.addCheckpoint(new Position(11, 0));
        return riskyCrossing;
    }

    /*
    public static Board makeRiskyCrossing() {
        HashMap<Position, Space> special = makeStartASpaces();
        // Making special spaces
        Position[] startingAt = new Position[] {new Position(4, 0), new Position(3, 8), new Position(12, 1), new Position(11, 9)};
        Heading[] startHeading = new Heading[] {SOUTH, EAST, WEST, NORTH};
        for (int i = 0; i < 4; i++) {
            Position position = startingAt[i];
            Heading direction = startHeading[i];
            addSpace(special, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
            position = Position.move(position, direction);
            addSpace(special, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
            position = Position.move(position, direction);
            direction = Heading.turnRight(direction);
            addSpace(special, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
            position = Position.move(position, direction);
            addSpace(special, new Obstacle(position, BLUE_CONVEYOR_BELT, direction));
        }

        addSpace(special, new Obstacle(new Position(8, 1), GREEN_CONVEYOR_BELT, WEST));
        addSpace(special, new Obstacle(new Position(7, 1), GREEN_CONVEYOR_BELT, WEST));
        addSpace(special, new Obstacle(new Position(6, 1), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(6, 2), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(6, 3), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(5, 3), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(5, 4), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(4, 4), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(4, 5), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(4, 6), GREEN_CONVEYOR_BELT, EAST));
        addSpace(special, new Obstacle(new Position(5, 6), GREEN_CONVEYOR_BELT, EAST));
        addSpace(special, new Obstacle(new Position(6, 6), GREEN_CONVEYOR_BELT, EAST));
        addSpace(special, new Obstacle(new Position(7, 6), GREEN_CONVEYOR_BELT, WEST));
        addSpace(special, new Obstacle(new Position(7, 7), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(7, 8), GREEN_CONVEYOR_BELT, EAST));
        addSpace(special, new Obstacle(new Position(8, 8), GREEN_CONVEYOR_BELT, EAST));
        addSpace(special, new Obstacle(new Position(9, 8), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(9, 7), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(9, 6), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(10, 6), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(10, 5), GREEN_CONVEYOR_BELT, SOUTH));
        addSpace(special, new Obstacle(new Position(11, 5), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(11, 4), GREEN_CONVEYOR_BELT, NORTH));
        addSpace(special, new Obstacle(new Position(11, 3), GREEN_CONVEYOR_BELT, WEST));
        addSpace(special, new Obstacle(new Position(10, 3), GREEN_CONVEYOR_BELT, WEST));
        addSpace(special, new Obstacle(new Position(9, 3), GREEN_CONVEYOR_BELT, WEST));
        addSpace(special, new Obstacle(new Position(8, 3), GREEN_CONVEYOR_BELT, EAST));
        addSpace(special, new Obstacle(new Position(8, 2), GREEN_CONVEYOR_BELT, NORTH));

        addSpace(special, new CheckPoint(new Position(8, 7), 0));
        addSpace(special, new CheckPoint(new Position(11, 0), 1));
        HashMap<Position, Heading[]> walls = makeStartAWalls();

        return makeCustomBoard(special, walls, 13, 10, "Risky Crossing");
    }
    */

    private static void addSpace(HashMap<Position, Space> spaces, Space space) {
        spaces.put(space.Position, space);
    }
}
