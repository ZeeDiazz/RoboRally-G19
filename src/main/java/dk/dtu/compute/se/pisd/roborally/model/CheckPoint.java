package dk.dtu.compute.se.pisd.roborally.model;


public class CheckPoint extends Space{
    public int counter;
    public Boolean checkpointFlagged;

/**
     * Constructor for CheckPoint
 *@param board the game board the space belong to.
  * @param x x-coordinate of the space on the board.
 * @param y y-coordinate of the space on the board.
 * @param counter counter for order of checkpoints
     * @author Felix Schmidt, s224313@dtu.dk
     */
    public CheckPoint(Board board, int x, int y, int counter){
        super(board,x,y);
        this.counter = counter;
        isCheckPoint = true;
        this.checkpointFlagged = false;
    }

    public int getCheckPointCounter(){return counter;}

    // TODO: setReboot() aka add checkpoint to player


    /**
     * This method sets the flag of a checkpoint to a boolean value
     * @param checkpointFlagged
     * @authoer Felix Schmidt
     */
    public void setCheckpointFlagged(Boolean checkpointFlagged) {
        this.checkpointFlagged = checkpointFlagged;
        notifyChange();
    }
    /**
     * get method for to see if a checkpoint is flagged
     * @return true if checkpoint is flagged, false if checkpoint is not flagged
     * @author Felix Schmidt, s224313@dtu.dk
     */
    public boolean getCheckpointFlagged(){
        return checkpointFlagged;
    }

    /**
     * This method adds to players checkpointcounter
     * @param player
     * @return players current checkpointcounter
     */
    public int addToPlayersCurrentCheckPointCounter(Player player){
        player.playersCurrentCheckpointCounter += counter;

        return player.playersCurrentCheckpointCounter;
    }

}
