package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController.performMove;


/**
 * Class used for executing commands
 *
 * @author Zigalow
 */
public class CommandExecuter {


    /**
     * Executes the action of a command card to the given player.
     *
     * @param player
     * @param command
     * @author Daniel, ZeeDiazz (Zaid), Zigalow
     */
    public void executeCardCommand(@NotNull Player player, Command command) {
        if (player.robot != null && command != null) {
            switch (command) {
                case MOVE_1, MOVE_2, MOVE_3, BACK_UP -> {
                    this.moveCommand(player.robot, command);
                    player.setPrevProgramming(command);
                }
                case LEFT, RIGHT, U_TURN -> {
                    this.turnRobotCommand(player.robot, command);
                    player.setPrevProgramming(command);
                }
                case POWER_UP -> {
                    player.addEnergyCube(1);
                    player.setPrevProgramming(command);
                }
                case AGAIN -> this.repeatPrevProgramming(player);

                default -> {
                }
                // DO NOTHING (for now)
            }
        }
    }

    /**
     * Execute a move command in the given robot
     *
     * @param robot
     * @param moveCommand
     * @author Zigalow
     */
    public void moveCommand(@NotNull Robot robot, Command moveCommand) {
        switch (moveCommand) {
            case MOVE_1 -> performMove(Move.fromRobot(robot, 1));
            case MOVE_2 -> performMove(Move.fromRobot(robot, 2));
            case MOVE_3 -> performMove(Move.fromRobot(robot, 3));
            case BACK_UP -> backUp(robot);
        }
    }

    /**
     * Execute the turning of the given robot
     *
     * @param robot
     * @param turnCommand
     * @author Zigalow
     */
    public void turnRobotCommand(@NotNull Robot robot, Command turnCommand) {
        switch (turnCommand) {
            case LEFT -> turnLeft(robot);
            case RIGHT -> turnRight(robot);
            case U_TURN -> turnAround(robot);
        }
    }

    /**
     * This method moves the robot one space back, without changing the robot's direction.
     *
     * @param robot
     * @author ZeeDiazz (Zaid)
     */
    public void backUp(@NotNull Robot robot) {
        HeadingDirection playerDirection = robot.getHeadingDirection();

        //get the opposite direction of the player
        HeadingDirection oppositeDirection = HeadingDirection.oppositeHeadingDirection(playerDirection);

        //move player by one the opposite side
        performMove(new Move(robot.getSpace().position, oppositeDirection, 1, robot));
    }

    /**
     * Turns the robots direction to the right
     *
     * @param robot
     * @author Daniel Weper Jensen
     */
    public void turnRight(@NotNull Robot robot) {
        HeadingDirection robotHeadingDirection = robot.getHeadingDirection();
        HeadingDirection newDirection = HeadingDirection.rightHeadingDirection(robotHeadingDirection);

        robot.setHeadingDirection(newDirection);
    }

    /**
     * Turns the robots direction to the left
     *
     * @param robot
     * @author Daniel Weper Jensen
     */
    public void turnLeft(@NotNull Robot robot) {
        HeadingDirection robotHeadingDirection = robot.getHeadingDirection();
        HeadingDirection newDirection = HeadingDirection.leftHeadingDirection(robotHeadingDirection);

        robot.setHeadingDirection(newDirection);
    }

    /**
     * This method turns players to the opposite direction, and the robot still remains in the current space - U-Turn
     *
     * @param robot
     * @autor ZeeDiazz (Zaid)
     */
    public void turnAround(@NotNull Robot robot) {
        HeadingDirection playerDirection = robot.getHeadingDirection();
        HeadingDirection newDirection = HeadingDirection.oppositeHeadingDirection(playerDirection);

        robot.setHeadingDirection(newDirection);
    }

    /**
     * This method is for the command Again which repeats the command from the previous register
     *
     * @param player
     * @author ZeeDiazz (Zaid)
     */
    public void repeatPrevProgramming(@NotNull Player player) {
        Command previousCommand = player.getPrevProgramming();
        executeCardCommand(player, previousCommand);
    }

}
