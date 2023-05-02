package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.spaces.EmptySpace;
import javafx.scene.layout.StackPane;

/**
 * @author ZeeDiazz (Zaid)
 */

public class ObstacleView extends StackPane implements ViewObserver{
        final public static int Obstacle_HEIGHT = 75; // 60; // 75;
        final public static int Obstacle_WIDTH = 75;  // 60; // 75;

    private final EmptySpace obstacle;


    public ObstacleView(EmptySpace obstacle) {
        this.obstacle = obstacle;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == obstacle) {
            // do nothing for now
        }
    }
}
