package dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view;

/**
 * ...
 * This class creates a black or white space on the game board with a specific height and width
 * if the space contains a player, a player is displayed as a polygon
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */


import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.HeadingDirection;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Robot;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;
    final public static double wallThickness = 3;
    final public static Color wallColor = Color.RED;

    public final Space space;

    /**
     * This method creates a new singular space
     *
     * @param space The space that will be viewed
     * @author ZeeDiazz (Zaid), Felix723, Zahed Wafa
     * Assigning different colors to different types of Obstacle
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

        String style;
        if (space instanceof BlueConveyorSpace) {
            style = "-fx-background-color: blue;";
        } else if (space instanceof GreenConveyorSpace) {
            style = "-fx-background-color: green;";
        } else if (space instanceof RedGearSpace) {
            style = "-fx-background-color: red;";
        } else if (space instanceof GreenGearSpace) {
            style = "-fx-background-color: darkgreen;";
        } else if (space instanceof PitSpace) {
            style = "-fx-background-color: black;";
        } else if (space instanceof CheckPointSpace) {
            style = "-fx-background-color: yellow;";
        } else if ((space.position.X + space.position.Y) % 2 == 0) {
            style = "-fx-background-color: white;";
        } else {
            style = "-fx-background-color: gray;";
        }
        this.setStyle(style);

        // Zahed Wafa {
        double top = space.hasWall(HeadingDirection.NORTH) ? wallThickness : 0;
        double right = space.hasWall(HeadingDirection.EAST) ? wallThickness : 0;
        double bottom = space.hasWall(HeadingDirection.SOUTH) ? wallThickness : 0;
        double left = space.hasWall(HeadingDirection.WEST) ? wallThickness : 0;

        BorderWidths borderWidths = new BorderWidths(top, right, bottom, left);
        BorderStroke borderStroke = new BorderStroke(wallColor, BorderStrokeStyle.SOLID, null, borderWidths);
        Border border = new Border(borderStroke);
        this.setBorder(border);
        // Zahed Wafa }

        // }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player;
        Robot robot = space.getRobot();
        
        if (robot != null && robot.getOwner() != null) {
            player = robot.getOwner();
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.robot.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.robot.getHeadingDirection().ordinal()) % 360);
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
