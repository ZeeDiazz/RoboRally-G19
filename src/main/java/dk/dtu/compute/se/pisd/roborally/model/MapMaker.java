package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.model.spaces.BlueConveyorSpace;
import dk.dtu.compute.se.pisd.roborally.model.spaces.GreenConveyorSpace;
import dk.dtu.compute.se.pisd.roborally.model.spaces.Space;
import java.util.HashMap;
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

    public static Board makeStartA() {
        HashMap<Position, Space> spaces = new HashMap<>();
        // Making special spaces
        // The spawn points
        /*
        addSpace(spaces, new Space(new Position(1, 1), true));
        addSpace(spaces, new Space(new Position(3, 2), true));
        addSpace(spaces, new Space(new Position(4, 1), true));
        addSpace(spaces, new Space(new Position(5, 1), true));
        addSpace(spaces, new Space(new Position(6, 2), true));
        addSpace(spaces, new Space(new Position(8, 1), true));
         */

        // The two green conveyors
        addSpace(spaces, new GreenConveyorSpace(new Position(0, 0), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(9, 0), NORTH));

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
            addSpace(spaces, new BlueConveyorSpace(position, direction));
            position = Position.move(position, direction);
            addSpace(spaces, new BlueConveyorSpace(position, direction));
            position = Position.move(position, direction);
            direction = Heading.turnRight(direction);
            addSpace(spaces, new BlueConveyorSpace(position, direction));
            position = Position.move(position, direction);
            addSpace(spaces, new BlueConveyorSpace(position, direction));
        }

        addSpace(spaces, new GreenConveyorSpace(new Position(5, 1), WEST));
        addSpace(spaces, new GreenConveyorSpace(new Position(4, 1), WEST));
        addSpace(spaces, new GreenConveyorSpace(new Position(3, 1), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(3, 2), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(3, 3), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(2, 3), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(2, 4), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(1, 4), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(1, 5), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(1, 6), EAST));
        addSpace(spaces, new GreenConveyorSpace(new Position(2, 6), EAST));
        addSpace(spaces, new GreenConveyorSpace(new Position(3, 6), EAST));
        addSpace(spaces, new GreenConveyorSpace(new Position(4, 6), WEST));
        addSpace(spaces, new GreenConveyorSpace(new Position(4, 7), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(4, 8), EAST));
        addSpace(spaces, new GreenConveyorSpace(new Position(5, 8), EAST));
        addSpace(spaces, new GreenConveyorSpace(new Position(6, 8), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(6, 7), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(6, 6), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(7, 6), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(7, 5), SOUTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(8, 5), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(8, 4), NORTH));
        addSpace(spaces, new GreenConveyorSpace(new Position(8, 3), WEST));
        addSpace(spaces, new GreenConveyorSpace(new Position(7, 3), WEST));
        addSpace(spaces, new GreenConveyorSpace(new Position(6, 3), WEST));
        addSpace(spaces, new GreenConveyorSpace(new Position(5, 3), EAST));
        addSpace(spaces, new GreenConveyorSpace(new Position(5, 2), NORTH));

        HashMap<Position, Heading[]> walls = new HashMap<>();

        return makeCustomBoard(spaces, walls, 10, 10, "5A");
    }

    public static Board make5B() {
        HashMap<Position, Space> spaces = new HashMap<>();

        // Making special spaces
        // The blue conveyor ring
        for (int y = 0; y < 8; y++) {
            addSpace(spaces, new BlueConveyorSpace(new Position(1, y), SOUTH));
        }
        addSpace(spaces, new BlueConveyorSpace(new Position(2, 0), SOUTH));
        for (int x = 2; x < 10; x++) {
            addSpace(spaces, new BlueConveyorSpace(new Position(x, 1), WEST));
        }
        addSpace(spaces, new BlueConveyorSpace(new Position(9, 2), WEST));
        for (int y = 2; y < 10; y++) {
            addSpace(spaces, new BlueConveyorSpace(new Position(8, y), NORTH));
        }
        addSpace(spaces, new BlueConveyorSpace(new Position(2, 0), NORTH));
        for (int x = 0; x < 8; x++) {
            addSpace(spaces, new BlueConveyorSpace(new Position(x, 8), EAST));
        }
        addSpace(spaces, new BlueConveyorSpace(new Position(0, 7), EAST));

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

    private static void addSpace(HashMap<Position, Space> spaces, Space space) {
        spaces.put(space.position, space);
    }
}
