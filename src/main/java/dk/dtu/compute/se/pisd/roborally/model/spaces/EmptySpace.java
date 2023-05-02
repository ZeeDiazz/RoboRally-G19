package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.List;

public class EmptySpace extends Subject {
    public final Board board;
    public final Position position;
    protected final ArrayList<Heading> walls;

    public EmptySpace(Board board, Position position, Heading... walls) {
        this.board = board;
        this.position = position;
        this.walls = new ArrayList<>(List.of(walls));
    }

    public EmptySpace(Board board, Position position) {
        this(board, position, new Heading[0]);
    }

    public void landedOn(Player player) {
        // do nothing
    }

    public Move endedRegisterOn(Player player, int registerIndex) {
        // do nothing
        return null;
    }

    public boolean canEnterFrom(Heading from) {
        return !walls.contains(from);
    }

    public boolean canExitBy(Heading going) {
        return !walls.contains(going);
    }

    /**
     * @param direction The direction to place the wall
     * @author Daniel Jensen
     * Add a wall to this space
     */
    public void addWall(Heading direction) {
        if (!walls.contains(direction)) {
            walls.add(direction);
            changed();
        }
    }

    // Hack
    public void changed() {
        notifyChange();
    }
}
