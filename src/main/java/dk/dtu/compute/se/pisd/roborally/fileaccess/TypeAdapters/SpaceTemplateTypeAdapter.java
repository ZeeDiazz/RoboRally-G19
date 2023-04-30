package dk.dtu.compute.se.pisd.roborally.fileaccess.TypeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.Transformer;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Obstacle;
import dk.dtu.compute.se.pisd.roborally.model.ObstacleType;


import java.io.IOException;


public class SpaceTemplateTypeAdapter extends TypeAdapter<SpaceTemplate> {
    @Override
    public void write(JsonWriter writer, SpaceTemplate spaceTemplate) throws IOException {
        writer.beginObject();
        writer.name("x").value(spaceTemplate.x);
        writer.name("y").value(spaceTemplate.y);

        // Write walls
        if (!spaceTemplate.walls.isEmpty()) {
            writer.name("walls");
            writer.beginArray();
            for (Heading wall : spaceTemplate.walls) {
                writer.value(wall.toString());
            }
            writer.endArray();
        }
        // Write obstacle
        if (spaceTemplate.obstacle != null) {
            writer.name("obstacle");
            writer.beginArray();
            writer.value(spaceTemplate.obstacle.getType().toString());
            writer.value(spaceTemplate.obstacle.getDirection().toString());
            writer.endArray();
        }

        // Write checkpoint
        if (spaceTemplate.checkPoint != null) {
            writer.name("checkPoint");
            writer.value(spaceTemplate.checkPoint.Id);

        }

        writer.endObject();
    }


    @Override
    public SpaceTemplate read(JsonReader reader) throws IOException {
        SpaceTemplate spaceTemplate = new SpaceTemplate();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("x")) {
                spaceTemplate.x = reader.nextInt();
            } else if (name.equals("y")) {
                spaceTemplate.y = reader.nextInt();
            } else if (name.equals("walls")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    String wallString = reader.nextString();
                    spaceTemplate.walls.add(Heading.valueOf(wallString));
                }
                reader.endArray();
            } else if (name.equals("obstacle")) {
                reader.beginArray();
                String typeString = reader.nextString();
                String directionString = reader.nextString();
                spaceTemplate.obstacle = new Obstacle(Transformer.initialBoard, spaceTemplate.x, spaceTemplate.y, ObstacleType.valueOf(typeString), Heading.valueOf(directionString));
                reader.endArray();
            } else if (name.equals("checkPoint")) {
                int id = reader.nextInt();
                spaceTemplate.checkPoint = new CheckPoint(Transformer.initialBoard, spaceTemplate.x, spaceTemplate.y, id);
            } 
        }
        reader.endObject();
        return spaceTemplate;
    }
}

