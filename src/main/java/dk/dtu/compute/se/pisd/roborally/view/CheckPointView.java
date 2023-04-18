package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.CheckPoint;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class CheckPointView extends StackPane implements ViewObserver {

    public final static int CHECKPOINT_HEIGHT = 25;
    public final static int CHECKPOINT_WIDTH = 10;

    public final CheckPoint checkPoint;



    public CheckPointView(@NotNull CheckPoint checkPoint){
        this.checkPoint = checkPoint;

        this.setPrefWidth(CHECKPOINT_WIDTH);
        this.setMinWidth(CHECKPOINT_WIDTH);
        this.setMaxWidth(CHECKPOINT_WIDTH);

        this.setPrefHeight(CHECKPOINT_HEIGHT);
        this.setMinHeight(CHECKPOINT_HEIGHT);
        this.setMaxHeight(CHECKPOINT_HEIGHT);


        if(checkPoint.getCheckpointFlagged()){
            this.setStyle("-fx-background-color: red");
        }else{
            this.setStyle("-fx-background-color: green");
        }

        checkPoint.attach(this);
        update(checkPoint);
    }

    @Override
    public void updateView(Subject subject) {
        if(checkPoint.getCheckpointFlagged()){
            // do something
        }
    }


}
