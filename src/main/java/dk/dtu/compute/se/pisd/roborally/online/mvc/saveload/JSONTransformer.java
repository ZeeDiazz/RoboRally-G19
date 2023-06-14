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


        JsonObject gameJsonObject = new JsonObject();

        gameJsonObject.add("priorityAntennaSpace", game.priorityAntennaSpace.serialize());

        if (game.currentInteractiveCard != null) {
            gameJsonObject.addProperty("currentInteractiveCard", game.currentInteractiveCard.toString());
        }
        gameJsonObject.addProperty("gameType", "LocalGame");
        gameJsonObject.addProperty("gameId", game.getGameId());
        gameJsonObject.addProperty("moveCounter", game.getMoveCounter());
        gameJsonObject.addProperty("playerCount", game.getPlayerCount());
        gameJsonObject.addProperty("step", game.getStep());
        gameJsonObject.addProperty("stepMode", game.isStepMode());
        gameJsonObject.addProperty("phase", game.getPhase().toString());
        gameJsonObject.addProperty("currentPlayer", game.getCurrentPlayer().getName());
        gameJsonObject.add("board", game.board.serialize());
        JsonArray jsonArrayPlayers = new JsonArray();


        List<Player> onlinePlayers = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            onlinePlayers.add(player);
        }


        List<LocalPlayer> rememberedLocalPlayers = new ArrayList<>();

        LocalPlayer localPlayer;

        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player onlinePlayer = onlinePlayers.remove(0);
            localPlayer = new LocalPlayer(null, onlinePlayer.getRobot().getColor(), onlinePlayer.getName());
            localPlayer.setPlayerID(onlinePlayer.getPlayerID());

            if (onlinePlayer.getPrevProgramming() != null) {
                localPlayer.setPrevProgramming(onlinePlayer.getPrevProgramming());
            }
            localPlayer.setProgramField(onlinePlayer.getProgram());
            localPlayer.setCards(onlinePlayer.getCards());
            localPlayer.setRobot(onlinePlayer.getRobot());
            jsonArrayPlayers.add(localPlayer.serialize());
            rememberedLocalPlayers.add(localPlayer);
        }

        gameJsonObject.add("players", jsonArrayPlayers);

        JsonArray jsonArrayPrioritisedPlayers = new JsonArray();

        for (Player onlinePlayer : game.prioritisedPlayers) {
            jsonArrayPrioritisedPlayers.add(onlinePlayer.getPlayerID());
        }
        gameJsonObject.add("prioritisedPlayers", jsonArrayPrioritisedPlayers);


        JsonObject jsonGameController = new JsonObject();

        jsonGameController.add("game", gameJsonObject);

        jsonGameController.addProperty("gameType", "LocalGame");
       
        
        JsonObject outerJson = new JsonObject();
        
        outerJson.add("gameController",jsonGameController);
        
        

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            fileWriter = new FileWriter(file);
            writer = gson.newJsonWriter(fileWriter);

            gson.toJson(gameJsonObject, writer);

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
