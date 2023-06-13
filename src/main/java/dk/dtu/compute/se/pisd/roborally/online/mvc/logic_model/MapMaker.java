package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MapMaker {
    /**
     * Creates a map from the given parameters
     *
     * @param specialSpaces
     * @param walls
     * @param spawnPositions
     * @param width
     * @param height
     * @param name
     * @return
     * @author Daniel & Zaid
     */
    public static Board makeCustomBoard(HashMap<Position, Space> specialSpaces, HashMap<Position, HeadingDirection[]> walls, List<Position> spawnPositions, int width, int height, String name) {
        Space[][] spaces = new Space[width][height];

        // Fill up the slots
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space;
                Position currentPosition = new Position(x, y);
                if (specialSpaces.containsKey(currentPosition)) {
                    space = specialSpaces.get(currentPosition);
                } else {
                    space = new Space(currentPosition);
                }
                spaces[x][y] = space;

                if (walls.containsKey(currentPosition)) {
                    for (HeadingDirection wallDirection : walls.get(currentPosition)) {
                        space.addWall(wallDirection);
                    }
                }
            }
        }
        return (spawnPositions.isEmpty()) ? new Board(spaces, name) : new Board(spaces, name, spawnPositions);
    }

    /**
     * Loads a map from json fil from the map given in the parameter
     *
     * @param map
     * @return
     * @throws FileNotFoundException
     * @author ZeeDiazz (Zaid)
     */
    public static Board loadJsonBoard(String map) throws FileNotFoundException {
        JsonParser parser = new JsonParser();

        // Load the json fil
        JsonElement mapFile = parser.parse(new FileReader("src/main/resources/boards/" + map + ".json"));
        JsonObject mapBoard = mapFile.getAsJsonObject();

        //Get the width and height of the map
        int width = mapBoard.get("width").getAsInt();
        int height = mapBoard.get("height").getAsInt();

        //All spaces
        JsonArray spaces = mapBoard.get("spaces").getAsJsonArray();

        //HashMap for all obstaclesSpaces in the map
        HashMap<Position, Space> obstacleSpaces = new HashMap<>();
        for (JsonElement obstacles : spaces) {
            JsonObject spaceObject = obstacles.getAsJsonObject();

            //Get the position of the obstacle
            int x = spaceObject.get("position").getAsJsonObject().get("x").getAsInt();
            int y = spaceObject.get("position").getAsJsonObject().get("y").getAsInt();
            Position position = new Position(x, y);

            // Create the appropriate space object based on the "type" field in the JSON
            Space space = null;
            String type = spaceObject.get("obstacleType").getAsString();

            switch (type) {
                case "BlueConveyorSpace" -> {
                    String heading = spaceObject.get("heading").getAsString();
                    HeadingDirection direction = getHeadingDirection(heading);
                    space = new BlueConveyorSpace(position, direction);
                }
                case "GreenConveyorSpace" -> {
                    String heading = spaceObject.get("heading").getAsString();
                    HeadingDirection direction = getHeadingDirection(heading);
                    space = new GreenConveyorSpace(position, direction);
                }
                case "RedGearSpace" -> {
                    String heading = spaceObject.get("heading").getAsString();
                    HeadingDirection direction = getHeadingDirection(heading);
                    space = new RedGearSpace(position, direction);
                }
                case "GreenGearSpace" -> {
                    String heading = spaceObject.get("heading").getAsString();
                    HeadingDirection direction = getHeadingDirection(heading);
                    space = new GreenGearSpace(position, direction);
                }
            }
            //put the obstacle in the hashmap
            obstacleSpaces.put(position, space);
        }

        JsonArray wallsArray = mapBoard.get("walls").getAsJsonArray();
        HashMap<Position, HeadingDirection[]> walls = new HashMap<>();
        for (JsonElement wallObstacle : wallsArray) {
            JsonObject wallObject = wallObstacle.getAsJsonObject();
            int x = wallObject.get("position").getAsJsonObject().get("x").getAsInt();
            int y = wallObject.get("position").getAsJsonObject().get("y").getAsInt();
            Position position = new Position(x, y);

            String heading = wallObject.get("heading").getAsString();
            HeadingDirection direction = getHeadingDirection(heading);

            walls.put(position, new HeadingDirection[]{direction});
        }
        List<Position> spawnPositions = new ArrayList<>();

        //To get the spawning position of the map
        if (map.contains("Start")) {
            JsonArray robotsArray = mapBoard.get("spawnPositions").getAsJsonArray();
            for (JsonElement robotPosition : robotsArray) {
                JsonObject positionObject = robotPosition.getAsJsonObject();
                int x = positionObject.get("x").getAsInt();
                int y = positionObject.get("y").getAsInt();
                spawnPositions.add(new Position(x, y));
            }
        }
        //Create a board with the obstacles and walls and the width and the board name
        return makeCustomBoard(obstacleSpaces, walls, spawnPositions, width, height, map);
    }

    /**
     * To get the heading of some of the spaces from json
     *
     * @param heading
     * @return
     * @author ZeeDiazz (Zaid)
     */
    private static HeadingDirection getHeadingDirection(String heading) {
        switch (heading) {
            case "NORTH":
                return HeadingDirection.NORTH;
            case "SOUTH":
                return HeadingDirection.SOUTH;
            case "EAST":
                return HeadingDirection.EAST;
            case "WEST":
                return HeadingDirection.WEST;
        }
        return null;
    }

    /**
     * Create Risky Crossing map
     *
     * @return
     * @throws FileNotFoundException
     * @author ZeeDiazz (Zaid)
     */
    public static Board makeJsonRiskyCrossing() throws FileNotFoundException {
        Board startA = loadJsonBoard("StartA");
        Board startB = loadJsonBoard("5A");
        Board riskyCrossing = Board.add(Board.rotateRight(startA), startB, new Position(3, 0), "RiskyCrossing");
        riskyCrossing.addCheckpoint(new Position(8, 7));
        riskyCrossing.addCheckpoint(new Position(11, 0));
        riskyCrossing.addPriorityAntenna(new Position(0, 4));
        return riskyCrossing;
    }

    /**
     * Create Dizzy Highway map
     *
     * @return
     * @throws FileNotFoundException
     * @author ZeeDiazz (Zaid)
     */
    public static Board makeJsonDizzyHighway() throws FileNotFoundException {
        Board startA = loadJsonBoard("StartA");
        Board startB = loadJsonBoard("5B");
        Board dizzyHighway = Board.add(Board.rotateRight(startA), startB, new Position(3, 0), "DizzyHighway");
        dizzyHighway.addCheckpoint(new Position(12, 3));
        dizzyHighway.addPriorityAntenna(new Position(0, 4));
        return dizzyHighway;
    }
}
