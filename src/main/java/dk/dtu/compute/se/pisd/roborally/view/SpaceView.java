/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Obstacle;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * ...
 * This class creates a black or white space on the game board with a specific height and width
 * if the space contains a player, a player is displayed as a polygon
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;
    final public static double wallThickness = 3;
    final public static Color wallColor = Color.RED;

    public final Space space;

    /**

     * This method creates a new singular space
     * @author ZeeDiazz (Zaid), Felix723
     * Assigning different colors to different types of Obstacle
     * @param space The space that will be viewed


     */
    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);


        //ZeeDiaz (Zaid){
        if(space instanceof Obstacle obstacle) {
            switch (obstacle.getType()){
                case BLUE_CONVEYOR_BELT :
                    this.setStyle("-fx-background-color: blue;");
                    break;
                case GREEN_CONVEYOR_BELT:
                    this.setStyle("-fx-background-color: green;");
                    break;
                case PUSH_PANEL:
                    break;
                case BOARD_LASER:
                    break;
                case GEAR:
                    break;
            }
        } //ZeeDiaz (Zaid)}

        else if(space instanceof CheckPoint checkPoint){
                this.setStyle("-fx-background-color: orange;");
            }

        else {
            if ((space.x + space.y) % 2 == 0) {
                this.setStyle("-fx-background-color: white;");
            } else {
                this.setStyle("-fx-background-color: black;");
            }
         }

        double top = space.hasWall(Heading.NORTH) ? wallThickness : 0;
        double right = space.hasWall(Heading.EAST) ? wallThickness : 0;
        double bottom = space.hasWall(Heading.SOUTH) ? wallThickness : 0;
        double left = space.hasWall(Heading.WEST) ? wallThickness : 0;

        BorderWidths borderWidths = new BorderWidths(top, right, bottom, left);
        BorderStroke borderStroke = new BorderStroke(wallColor, BorderStrokeStyle.SOLID, null, borderWidths);
        Border border = new Border(borderStroke);
        this.setBorder(border);


       // }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }
}
