package dk.dtu.compute.se.pisd.roborally.online.mvc.saveload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Board;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.LocalGame;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.OnlineGame;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for handling of the save/load game feature
 *
 * @author Zigalow
 */
public class JSONTransformer {

    private static GameController currentGameController;

    public JSONTransformer(GameController gameController) {
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
     * @param file The json file which should be loaded
     * @return The board which is loaded from the json file
     * @author Zigalow, Daniel
     */

    public static GameController loadBoard(File file) {
        JsonParser parser = new JsonParser();

        try {
            JsonObject json = (JsonObject) parser.parse(new FileReader(file));

            String gameType = json.get("gameController").getAsJsonObject().get("gameType").getAsString().equals("OnlineGame") ? "Online" : "Offline";


            Game game;

            if (gameType.equals("Online")) {
                game = new OnlineGame(new Board(10, 10), 9);
            } else {
                game = new LocalGame(new Board(10, 10));
            }

            GameController gameController = new GameController(game);

            return (GameController) gameController.deserialize(json.get("gameController").getAsJsonObject());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
