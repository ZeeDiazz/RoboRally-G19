package dk.dtu.compute.se.pisd.roborally.online.mvc.saveload;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static void saveBoard(File file, Game game) {
        JsonObject gameSerialization = game.serialize().getAsJsonObject();
        // change all the types from remote to local
        gameSerialization.addProperty("gameType", "LocalGame");
        JsonArray gamePlayers = gameSerialization.get("players").getAsJsonArray();
        int playerCount = gamePlayers.size();
        for (int i = 0; i < playerCount; i++) {
            JsonObject gamePlayer = gamePlayers.get(0).getAsJsonObject();
            gamePlayers.remove(0);
            gamePlayer.addProperty("playerType", "LocalPlayer");
            gamePlayers.add(gamePlayer);
        }
        gameSerialization.add("players", gamePlayers);

        JsonObject gameJsonObject = new JsonObject();
        gameJsonObject.addProperty("gameType", "LocalGame");
        gameJsonObject.add("game", gameSerialization);

        JsonObject outerJson = new JsonObject();
        outerJson.add("gameController", gameJsonObject);

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fileWriter = new FileWriter(file);
            writer = gson.newJsonWriter(fileWriter);

            gson.toJson(outerJson, writer);

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
        System.out.println("Loading");
        JsonParser parser = new JsonParser();

        try {
            System.out.println("Reading file");
            JsonObject json = (JsonObject) parser.parse(new FileReader(file));

            String gameType = json.get("gameController").getAsJsonObject().get("gameType").getAsString().equals("OnlineGame") ? "Online" : "Offline";


            Game game;

            if (gameType.equals("Online")) {
                game = new OnlineGame(new Board(10, 10), 9);
            } else {
                game = new LocalGame(new Board(10, 10));
            }

            GameController gameController = new GameController(game);

            System.out.println("Deserializing");
            return (GameController) gameController.deserialize(json.get("gameController").getAsJsonObject());

        } catch (IOException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
        return null;
    }

    public static GameController loadBoard(File file, Object object) {
        JsonParser parser = new JsonParser();

        try {
            JsonObject json = (JsonObject) parser.parse(new FileReader(file));

            Game game = new LocalGame(new Board(10, 10));
            game = (Game) game.deserialize(json);

            GameController gameController = new GameController(game);

            return gameController;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
