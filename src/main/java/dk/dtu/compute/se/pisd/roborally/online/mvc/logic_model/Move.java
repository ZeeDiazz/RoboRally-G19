package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

/**
 * Move class is used for move action
 *
 * @author Daniel
 */
public final class Move {
    public final Position start;
    public final HeadingDirection direction;
    public final int amount;
    public final Robot moving;

    /**
     * Constructor for Move, with a start position, the heading and the mount of moves the player needs.
     *
     * @param start
     * @param headingDirection
     * @param amount
     * @param moving
     * @author Daniel
     */

    public Move(Position start, HeadingDirection headingDirection, int amount, Robot moving) {
        this.start = start;
        this.direction = headingDirection;
        this.amount = amount;
        this.moving = moving;
    }

    public Position getEndingPosition() {
        return Position.move(start, direction, amount);
    }

    /**
     * Move x amount of from the robot
     *
     * @param robot
     * @param amount
     * @return
     * @author Daniel
     */
    public static Move fromRobot(Robot robot, int amount) {
        return new Move(robot.getSpace().position, robot.getHeadingDirection(), amount, robot);
    }
}
