package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
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

        // Write player
        /*if (spaceTemplate.player != null) {
            writer.name("player");
            Gson gson = new GsonBuilder().create();
            gson.toJson(spaceTemplate.player, Player.class, writer);
        }*/

        writer.endObject();
    }


    @Override
    public SpaceTemplate read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
