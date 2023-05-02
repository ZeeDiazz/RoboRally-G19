package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Transformer {

    public static Board initialBoard;

    private static GameController currentGameController;


    public Transformer(GameController gameController) {
        currentGameController = gameController;
    }

    public static void saveBoard(Board board, File file) {
      /*  BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        initialBoard = board;


        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);

                boolean include = false;

                SpaceTemplate spaceTemplate = new SpaceTemplate();
                spaceTemplate.x = i;
                spaceTemplate.y = j;


                
                
               *//* if (space.hasPlayer()) {
                    spaceTemplate.player = space.getPlayer();
                }*//*
                if (!space.getWalls().isEmpty()) {
                    spaceTemplate.walls.addAll(space.getWalls());
                    include = true;
                }
                if (space instanceof Obstacle) {
                    Obstacle obstacle = (Obstacle) space;

                    spaceTemplate.obstacle = obstacle;
                    include = true;
                }
                if (space instanceof CheckPoint) {

                    CheckPoint checkPoint = (CheckPoint) space;

                    spaceTemplate.checkPoint = checkPoint;
                    include = true;
                }
                if (include) {
                    template.spaces.add(spaceTemplate);
                }
            }
        }*/


        JsonObject saveFileJson = new JsonObject();

        saveFileJson.add("gameController", currentGameController.serialize());


        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {


            //  Gson gson = new GsonBuilder().registerTypeAdapter(SpaceTemplate.class, new SpaceTemplateTypeAdapter()).setPrettyPrinting().create();

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

    public static Board loadBoard(File file) {
        // Todo implement
        return null;
/*
        // In simple cases, we can create a Gson object with new Gson():
        Gson gson = new GsonBuilder().registerTypeAdapter(SpaceTemplate.class, new SpaceTemplateTypeAdapter()).create();


        Board result = null;
        FileReader fileReader = null;
        JsonReader reader = null;
        try {
            fileReader = new FileReader(file);

            reader = gson.newJsonReader(fileReader);
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);


            result = new Board(template.width, template.height);
            initialBoard = result;

            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();

                } catch (IOException e2) {
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e2) {
                }
            }
        }
        return result;*/
    }


    public GameController getCurrentGameController() {
        return currentGameController;
    }
}


