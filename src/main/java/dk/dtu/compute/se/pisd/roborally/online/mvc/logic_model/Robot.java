package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import org.jetbrains.annotations.NotNull;

public class Robot extends Subject {

    public int checkpointReached = 0;

    private Space space;
    private String color;

    public Player owner;


    public Position rebootPosition;
    private HeadingDirection headingDirection = HeadingDirection.SOUTH;

//    private CommandCardField[] program;
//    private CommandCardField[] cards;
//    private Command prevProgramming;

    //methods = damage, get pos, move


    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setRobot(null);
            }
            if (space != null) {
                space.setRobot(this);
            }
            notifyChange();
        }
    }

    public HeadingDirection getHeadingDirection() {
        return headingDirection;
    }

    /**
     * Sets the absalute direction of the player.
     *
     * @param headingDirection the new direction (headingDirection) to be set.
     */
    public void setHeadingDirection(@NotNull HeadingDirection headingDirection) {
        if (headingDirection != this.headingDirection) {
            this.headingDirection = headingDirection;
            notifyChange();
            if (space != null) {
                space.changed();
            }
        }
    }

    public void setRebootPosition(Position position) {
        this.rebootPosition = position;
    }

    public Position getRebootPosition() {
        return rebootPosition;
    }


    /**
     * Reboot the player, setting their position to their reboot space (latest collected checkpoint)
     * @author Daniel Jensen
     */
    public void reboot() {
        // TODO fix
        notifyChange();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.changed();
        }
    }

    public Player getOwner() {
        return owner;
    }
}

