package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.List;

public class Space extends Subject {
    public final Board board;
    public final Position position;
    protected final ArrayList<Heading> walls;
    protected Player standingOn;

    public Space(Board board, Position position, Heading... walls) {
        this.board = board;
        this.position = position;
        this.walls = new ArrayList<>(List.of(walls));
    }

    public Space(Board board, Position position) {
        this(board, position, new Heading[0]);
    }

    public void landedOn(Player player) {
        // do nothing
    }

    public Move endedRegisterOn(Player player, int registerIndex) {
        // do nothing
        return null;
    }

    public boolean hasWall(Heading direction) {
        return walls.contains(direction);
    }

    public boolean canEnterFrom(Heading from) {
        return !hasWall(from);
    }

    public boolean canExitBy(Heading going) {
        return !hasWall(going);
    }

    /**
     * @param direction The direction to place the wall
     * @author Daniel Jensen
     * Add a wall to this space
     */
    public void addWall(Heading direction) {
        if (!hasWall(direction)) {
            walls.add(direction);
            changed();
        }
    }

    // Hack
    public void changed() {
        notifyChange();
    }

    // TODO refactor so these aren't necessary?
    public void setPlayer(Player player) {
        this.standingOn = player;
    }
    public Player getPlayer() {
        return standingOn;
    }
}
