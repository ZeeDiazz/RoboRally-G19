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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;
    final public static double wallThickness = 4;
    final public static Color wallColor = Color.YELLOW;

    public final Space space;

    ImageView imageView;

    /**
     * This method creates a new singular space and loads the image of the spaces
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

        this.imageView = new ImageView();
        this.imageView.setFitWidth(SPACE_WIDTH);
        this.imageView.setFitHeight(SPACE_HEIGHT);


        if (space instanceof BlueConveyorSpace) {
            Image image = new Image("BlueConveyorSpace.png");
            this.imageView = new ImageView(image);

            switch (((BlueConveyorSpace) space).getDirection()){
                case NORTH -> imageView.setRotate(0);
                case SOUTH -> imageView.setRotate(180);
                case EAST -> imageView.setRotate(90);
                case WEST -> imageView.setRotate(270);
            }
            setSpaceImage(imageView);
        } else if (space instanceof GreenConveyorSpace) {
            Image image = new Image("GreenConveyorSpace.png");
            this.imageView = new ImageView(image);
            switch (((GreenConveyorSpace) space).getDirection()){
                case NORTH -> imageView.setRotate(0);
                case SOUTH -> imageView.setRotate(180);
                case EAST -> imageView.setRotate(90);
                case WEST -> imageView.setRotate(270);
            }
            setSpaceImage(imageView);

        } /*else if (space instanceof PriorityAntennaSpace) {
            Image image = new Image("PriorityAntennaSpace.png");
            this.imageView = new ImageView(image);
            setSpaceImage(imageView);
        }*/else if (space instanceof RedGearSpace) {
            Image image = new Image("RedGearSpace.png");
            this.imageView = new ImageView(image);
            setSpaceImage(imageView);
        } else if (space instanceof GreenGearSpace) {
            Image image = new Image("GreenGearSpace.png");
            this.imageView = new ImageView(image);
            setSpaceImage(imageView);
        } else if (space instanceof PitSpace) {
            Image image = new Image("PitSpace.png");
            this.imageView = new ImageView(image);
            setSpaceImage(imageView);
        } else if (space instanceof CheckPointSpace) {
            Image image = new Image("CheckPointSpace"+((CheckPointSpace) space).id+".png");
            this.imageView = new ImageView(image);
            setSpaceImage(imageView);
        } else {
            Image image = new Image("Space.png");
            this.imageView = new ImageView(image);
            setSpaceImage(imageView);
        }

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

    /**
     * Sets the image of the spaces
     * @param imageView that holds the image
     * @author ZeeDiazz (Zaid)
     */
    private void setSpaceImage(ImageView imageView){
        Pane pane = new Pane(imageView);

        BackgroundImage backgroundImage = new BackgroundImage(
                pane.snapshot(null, null),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(SPACE_WIDTH, SPACE_HEIGHT, false, false, false, false));

        setBackground(new Background(backgroundImage));
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }
}
