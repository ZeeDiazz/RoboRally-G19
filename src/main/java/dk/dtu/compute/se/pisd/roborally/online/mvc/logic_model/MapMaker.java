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
     * Loads a map from json fil from the map given in the parameter
     *
     * @param mapName
     * @return
     * @throws FileNotFoundException
     * @author ZeeDiazz (Zaid)
     */
    public static Board loadJsonBoard(String mapName) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement mapFile = parser.parse(new FileReader("src/main/resources/boards/" + mapName + ".json"));
        JsonObject mapBoard = mapFile.getAsJsonObject();
        Board board = new Board(9, 9);
        return (Board) board.deserialize(mapBoard);
    }

    /**
     * Create Risky Crossing map
     *
     * @return
     * @throws FileNotFoundException
     * @author ZeeDiazz (Zaid)
     */
    public static Board makeJsonRiskyCrossing() throws FileNotFoundException {
        return loadJsonBoard("RiskyCrossing");
    }

    /**
     * Create Dizzy Highway map
     *
     * @return
     * @throws FileNotFoundException
     * @author ZeeDiazz (Zaid)
     */
    public static Board makeJsonDizzyHighway() throws FileNotFoundException {
        return loadJsonBoard("DizzyHighway");
    }
}
