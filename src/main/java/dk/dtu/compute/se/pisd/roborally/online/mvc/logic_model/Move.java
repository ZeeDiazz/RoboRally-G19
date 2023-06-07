package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

public final class Move {
    public final Position start;
    public final HeadingDirection direction;
    public final int amount;
    public final Robot moving;

    public Move(Position start, HeadingDirection headingDirection, int amount, Robot moving) {
        this.start = start;
        this.direction = headingDirection;
        this.amount = amount;
        this.moving = moving;
    }
    
    

    public Position getEndingPosition() {
        return Position.move(start, direction, amount);
    }

    public static Move fromRobot(Robot robot, int amount) {
        return new Move(robot.getSpace().position, robot.getHeadingDirection(), amount, robot);
    }
}
