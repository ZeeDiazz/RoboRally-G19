package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import java.io.*;


/**
 * Class for handling of the save/load game feature
 * @author Zigalow
 */
public class Transformer {
    
    private static GameController currentGameController;
    
    public Transformer(GameController gameController) {
        currentGameController = gameController;
    }

    /**
     * @param file The json file where the serialization should be stored 
     * @author Zigalow
     */
    public static void saveBoard(File file) {
        JsonObject saveFileJson = new JsonObject();

        saveFileJson.add("gameController", currentGameController.serialize());


        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fileWriter = new FileWriter(file);
            writer = gson.newJsonWriter(fileWriter);

            gson.toJson(saveFileJson, writer);

            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {
                }
            }
        }
    }

    /**
     * 
     * @param file The json file which should be loaded
     * @return The board which is loaded from the json file
     * @author Zigalow, Daniel
     */
    
    public static Board loadBoard(File file) {
        JsonParser parser = new JsonParser();

        try {
            JsonObject json = (JsonObject) parser.parse(new FileReader(file));
            Board board = new Board(1, 1);
            return (Board) board.deserialize(json.get("gameController").getAsJsonObject().get("board"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


