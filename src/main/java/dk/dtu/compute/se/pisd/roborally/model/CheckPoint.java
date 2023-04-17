package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import java.awt.*;


public class CheckPoint extends Space{
    public Boolean isCheckPoint;
    public Boolean checkpointFlagged;

    public CheckPoint(Board board, int x, int y){
        super(board,x,y);
        isCheckPoint = true;
        this.checkpointFlagged = false;
    }

    public void setCheckpointFlagged(Boolean checkpointFlagged) {
        this.checkpointFlagged = checkpointFlagged;
        notifyChange();
    }

    public boolean getCheckpointFlagged(){
        return checkpointFlagged;
    }

}



/**
     * Constructor for CheckPoint
     * @param space
     * @author Felix Schmidt, s224313@dtu.dk
     */

/**
     * get method for to see if a checkpoint is flagged
     * @return true if checkpoint is flagged, false if checkpoint is not flagged
     * @author Felix Schmidt, s224313@dtu.dk
     */
/**
     * set method for boolean checkpointflagged
     * @param checkpointFlagged
     * @auther Felix Schmidt, s224313@gmail.com
     */
