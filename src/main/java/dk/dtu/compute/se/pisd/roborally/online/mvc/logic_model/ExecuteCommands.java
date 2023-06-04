package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;


import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController.performMove;


public class ExecuteCommands {
    private static Board board;

    public ExecuteCommands(Board board) {
        this.board = board;
    }

    /**
     * @param player
     * @param command
     * @author Daniel, ZeeDiazz (Zaid)
     */

    public void executeCardCommand(@NotNull Player player, Command command) {
        if (player.robot != null && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

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
                case AGAIN ->
                    //TODO: update AGAIN after the implementation of damage card and upgrade
                        this.repeatPrevProgramming(player);

                //player.setPrevProgramming(command);
                //}ZeeDiazz (Zaid)
                default -> {
                }
                // DO NOTHING (for now)
            }
        }
    }


    public void moveCommand(@NotNull Robot robot, Command moveCommand) {
        switch (moveCommand) {
            case MOVE_1 -> performMove(Move.fromRobot(robot, 1));
            case MOVE_2 -> performMove(Move.fromRobot(robot, 2));
            case MOVE_3 -> performMove(Move.fromRobot(robot, 3));
            case BACK_UP -> backUp(robot);
        }
    }

    public void turnRobotCommand(@NotNull Robot robot, Command turnCommand) {
        switch (turnCommand) {
            case LEFT -> turnLeft(robot);
            case RIGHT -> turnRight(robot);
            case U_TURN -> turnAround(robot);
        }
    }


    public void backUp(@NotNull Robot robot) {
        HeadingDirection playerDirection = robot.getHeadingDirection();

        //get the opposite direction of the player
        HeadingDirection oppositeDirection = HeadingDirection.oppositeHeadingDirection(playerDirection);

        //move player by one the opposite side
        performMove(new Move(robot.getSpace().position, oppositeDirection, 1, robot));
    }

    public void turnRight(@NotNull Robot robot) {
        HeadingDirection robotHeadingDirection = robot.getHeadingDirection();
        HeadingDirection newDirection = HeadingDirection.rightHeadingDirection(robotHeadingDirection);

        robot.setHeadingDirection(newDirection);
    }

    public void turnLeft(@NotNull Robot robot) {
        HeadingDirection robotHeadingDirection = robot.getHeadingDirection();
        HeadingDirection newDirection = HeadingDirection.leftHeadingDirection(robotHeadingDirection);

        robot.setHeadingDirection(newDirection);
    }

    public void turnAround(@NotNull Robot robot) {
        HeadingDirection playerDirection = robot.getHeadingDirection();
        HeadingDirection newDirection = HeadingDirection.oppositeHeadingDirection(playerDirection);

        robot.setHeadingDirection(newDirection);
    }

    /**
     * @param player
     * @author ZeeDiazz (Zaid)
     * This method is for the command Again, and repeat the programming from previous register
     */
    public void repeatPrevProgramming(@NotNull Player player) {
        Command previousCommand = player.getPrevProgramming();
        //if(previousCommand != Command.AGAIN) {
        executeCardCommand(player, previousCommand);
        //}
    }

}
