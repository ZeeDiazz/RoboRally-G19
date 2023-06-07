package dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view;

import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.CheckPointSpace;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

public class CheckPointView extends StackPane implements ViewObserver {

    public final static int CHECKPOINT_HEIGHT = 25;
    public final static int CHECKPOINT_WIDTH = 10;

    public final CheckPointSpace checkPoint;



    public CheckPointView(@NotNull CheckPointSpace checkPoint){
        this.checkPoint = checkPoint;

        this.setPrefWidth(CHECKPOINT_WIDTH);
        this.setMinWidth(CHECKPOINT_WIDTH);
        this.setMaxWidth(CHECKPOINT_WIDTH);

        this.setPrefHeight(CHECKPOINT_HEIGHT);
        this.setMinHeight(CHECKPOINT_HEIGHT);
        this.setMaxHeight(CHECKPOINT_HEIGHT);


        this.setStyle("-fx-background-color: red");

        checkPoint.attach(this);
        update(checkPoint);
    }

    @Override
    public void updateView(Subject subject) {
        /*
        if(checkPoint.getCheckpointFlagged()){
            // do something
        }
         */
    }


}
