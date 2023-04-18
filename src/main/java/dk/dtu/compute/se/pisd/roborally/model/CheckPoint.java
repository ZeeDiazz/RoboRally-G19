package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import java.awt.*;


public class CheckPoint extends Space{
    public int counter;
    public Boolean checkpointFlagged;

    public CheckPoint(Board board, int x, int y, int counter){
        super(board,x,y);
        this.counter = counter;
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

// To implement in board class
// CheckPoint checkPoint = new CheckPoint(this,3,4);
//        setSpace(checkPoint,3,5);

// Counter i constructor
// this.getCurrentplayer.setResetPoint(this.player.currentSpace)
