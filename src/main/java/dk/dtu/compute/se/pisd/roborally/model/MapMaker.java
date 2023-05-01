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

    private static HashMap<Position, Space> makeStartASpaces() {
        HashMap<Position, Space> spaces = new HashMap<>();
        // Making special spaces
        // The spawn points
        addSpace(spaces, new Space(new Position(1, 1), true));
        addSpace(spaces, new Space(new Position(0, 3), true));
        addSpace(spaces, new Space(new Position(1, 4), true));
        addSpace(spaces, new Space(new Position(1, 5), true));
        addSpace(spaces, new Space(new Position(0, 6), true));
        addSpace(spaces, new Space(new Position(1, 8), true));

        // The two green conveyors
        addSpace(spaces, new Obstacle(new Position(2, 0), GREEN_CONVEYOR_BELT, EAST));
        addSpace(spaces, new Obstacle(new Position(2, 9), GREEN_CONVEYOR_BELT, EAST));

        return spaces;
    }

    private static HashMap<Position, Heading[]> makeStartAWalls() {
        HashMap<Position, Heading[]> walls = new HashMap<>();
        walls.put(new Position(1, 2), new Heading[] {NORTH});
        walls.put(new Position(1, 7), new Heading[] {SOUTH});
        walls.put(new Position(2, 4), new Heading[] {EAST});
        walls.put(new Position(2, 5), new Heading[] {EAST});
        return walls;
    }

    public static Board makeDizzyHighway() {
        HashMap<Position, Space> special = makeStartASpaces();

        // Making special spaces
        // The blue conveyor ring
        for (int y = 0; y < 8; y++) {
            addSpace(special, new Obstacle(new Position(4, y), BLUE_CONVEYOR_BELT, SOUTH));
        }
        addSpace(special, new Obstacle(new Position(5, 0), BLUE_CONVEYOR_BELT, SOUTH));
        for (int x = 5; x < 13; x++) {
            addSpace(special, new Obstacle(new Position(x, 1), BLUE_CONVEYOR_BELT, WEST));
        }
        addSpace(special, new Obstacle(new Position(12, 2), BLUE_CONVEYOR_BELT, WEST));
        for (int y = 2; y < 10; y++) {
            addSpace(special, new Obstacle(new Position(11, y), BLUE_CONVEYOR_BELT, NORTH));
        }
        addSpace(special, new Obstacle(new Position(5, 0), BLUE_CONVEYOR_BELT, NORTH));
        for (int x = 3; x < 11; x++) {
            addSpace(special, new Obstacle(new Position(x, 8), BLUE_CONVEYOR_BELT, EAST));
        }
        addSpace(special, new Obstacle(new Position(3, 7), BLUE_CONVEYOR_BELT, EAST));
        addSpace(special, new CheckPoint(new Position(12, 3), 0));

        HashMap<Position, Heading[]> walls = makeStartAWalls();
        walls.put(new Position(6, 3), new Heading[] {NORTH});
        walls.put(new Position(6, 6), new Heading[] {WEST});
        walls.put(new Position(9, 3), new Heading[] {EAST});
        walls.put(new Position(9, 6), new Heading[] {SOUTH});

        return makeCustomBoard(special, walls, 13, 10, "Dizzy Highway");
    }

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

    private static void addSpace(HashMap<Position, Space> spaces, Space space) {
        spaces.put(space.Position, space);
    }
}
