/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *This class is responsible for the users interaction with:
 * game phases, execution of cards, execution of steps, movement, and who's the current player.
 * @author Ekkart Kindler, ekki@dtu.dk
 */

public class GameController {

    final public Board board;

    /**

     * @author Zigalow
     * This attribute is relating to the interactive cards. The property of this attribute will be set to the latest interactive card from a register.
     * This is also so that the PlayerView class is able to access the interactive card in question
     *
     *
     */
    public Command currentInteractiveCard;


    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */

    /**
     * @author Zigalow
     * This method makes it possible to click on a space, and make the current player's robot move to that space
     * @param space The space which the player's robot is going to be moved to
     *
     */

    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        Player currentPlayer = board.getCurrentPlayer();


        if (spaceIsOccupied(space)) {
            return;
        } else {
            space.setPlayer(currentPlayer);
        }
        nextPlayer(currentPlayer);
    }



    /***
     * This method starts the programming phase.
     * it does this by setting the current phase to PROGRAMMING, setting the current player and resets the steps.
     *
     */

    /**
     * @author Zigalow
     * This method is for checking whether a space is being occupied by a robot
     * @param space The space which is checked whether it's being occupied by a robot
     * @return Returns true if there is another robot on the space received as parameter
     */

    public boolean spaceIsOccupied(Space space) {
        return space.getPlayer() != null;
    }


    // XXX: V2

    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2

    /**
     * This methods finish or ends the programming phase.
     * It does this by changing the fields(spaces) visibility and setting the current phase to the next(ACTIVATION).
     * And again resetting the steps.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }


    /**
     * This method executes the next steps while setting the StepMode to false.
     */


    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }


    /**
     * This method executes the next steps while setting the StepMode to true
     */


    /**
     * @author Zigalow
     * This method starts the Player Interaction phase
     * @param options Refers to the command of the interactive card
     */




    // XXX: V3
    private void startPlayerInteractionPhase(Command options) {
        this.currentInteractiveCard = options;
        this.board.setPhase(Phase.PLAYER_INTERACTION);
    }


    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }


    // XXX: V2/V3
    private void continuePrograms() {
        do {
            executeNextStep();
            if (this.board.getPhase() != Phase.PLAYER_INTERACTION) {
                nextPlayer(board.getCurrentPlayer());
            } else {
                return;
            }
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }


    // XXX: V2/V3
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (card.command.isInteractive()) {
                        startPlayerInteractionPhase(command);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }

             /*   int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }*/
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**

     * @author Zigalow
     * This method is for executing an interactive card, where a player has chosen what command to execute
     * <p>If all the programs was chosen to be executed before the interactive card,
     * they will continue to do so after an option has been chosen</p>
     * @param option The command which the player has chosen to execute
     */
    public void executeCommandOptionAndContinue(Command option) {
        executeCommand(board.getCurrentPlayer(), option);
        this.board.setPhase(Phase.ACTIVATION);
        nextPlayer(this.board.getCurrentPlayer());
        if (!this.board.isStepMode()) {
            this.executePrograms();
        }
    }



    // TODO Assignment V2
    public void moveForward(@NotNull Player player) {
        move(player, player.getHeading(), 1);
    }

    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        move(player, player.getHeading(), 2);
    }


    private void move(@NotNull Player player, Heading playerDirection, int amount) {
        Space currentSpace = player.getSpace();
        Space newSpace = currentSpace;

        for (int i = 0; i < amount; i++) {
            newSpace = player.board.getNeighbour(newSpace, playerDirection);
        }

        if (spaceIsOccupied(newSpace)) {
            return;
        }

        player.setSpace(newSpace);
    }

    /**
     * This method turns the player to the right regardless of the direction the player are currently facing.
     *
     * @param player is the player that will perform the turn
     */
    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {
        Heading playerDirection = player.getHeading();

        Heading newDirection;
        switch (playerDirection) {
            case SOUTH -> newDirection = Heading.WEST;
            case NORTH -> newDirection = Heading.EAST;
            case WEST -> newDirection = Heading.NORTH;
            case EAST -> newDirection = Heading.SOUTH;
            default -> newDirection = playerDirection;
        }

        player.setHeading(newDirection);
    }

    /**
     * This method turns the player to the left regardless of the direction the player is currently facing.
     *
     * @param player is the player that will perform the turn
     */
    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {
        Heading playerDirection = player.getHeading();

        Heading newDirection;
        switch (playerDirection) {
            case SOUTH -> newDirection = Heading.EAST;
            case NORTH -> newDirection = Heading.WEST;
            case WEST -> newDirection = Heading.SOUTH;
            case EAST -> newDirection = Heading.NORTH;
            default -> newDirection = playerDirection;
        }

        player.setHeading(newDirection);
    }

    /**
     * This method override the target card with the destination card, effectively moving the card.
     *
     * @param source is the command card that will be moved
     * @param target is the empty command card the source card will be moved to
     * @return True if the cards were moved successfully, else false
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    /**

     * @author Zigalow, Daniel
     * This method relates to all that has to do with passing on the turn to the next player
     * <p>If the last player has executed his/her last command, the programming phase will start</p>
     * @param currentPlayer The current turn's player before the end of a turn
     *
     */


    public void nextPlayer(Player currentPlayer) {
        this.board.increaseMoveCounter();
        // Daniel {
        int currentStep = this.board.getStep();
        int nextPlayerNumber = this.board.getPlayerNumber(currentPlayer);
        nextPlayerNumber++;
        if (nextPlayerNumber >= this.board.getPlayersNumber()) {
            nextPlayerNumber = 0;
            currentStep++;
            if (currentStep < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(currentStep);

                //ZeeDiazz (Zaid){
                obstacleAction(currentPlayer);
   

                //Felix723 (Felix Schmidt){
                for (int i = 0; i < board.getPlayersNumber(); i++) {
                    if (board.getPlayer(i).getSpace() instanceof CheckPoint checkPoint) {
                        // hvis det checkpoint spilleren er på er det første og spillerens checkpoint er 0
                        if(checkPoint.counter == 0 && board.getPlayer(i).playersCurrentCheckpointCounter == 0){/*board.getPlayer(i).playersCurrentCheckpoint == checkPoint.getCheckPointCounter()*/
                            board.getPlayer(i).playersCurrentCheckpointCounter = 1;//checkPoint.getCheckPointCounter()

                        } else if (checkPoint.counter == 1 && board.getPlayer(i).playersCurrentCheckpointCounter == 1){
                            board.getPlayer(i).playersCurrentCheckpointCounter = 2; //checkPoint.getCheckPointCounter()
                            //skifter farve some placeholder for at man har nået sidste checkpoint
                            board.getPlayer(i).setColor("orange");

                        } {

                        }
                    }
                }
                //Felix723 (Felix Schmidt)}


                board.setStep(currentStep);
            } else {
                startProgrammingPhase();
            }

        // Daniel }

        }
        this.board.setCurrentPlayer(this.board.getPlayer(nextPlayerNumber));
    }

    /**
     * @author ZeeDiazz (Zaid)
     *  It checks if a player is on an obstacle, and executes the obstacles action.
     * @param currentPlayer
     */
    public void obstacleAction(Player currentPlayer) {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i).getSpace() instanceof Obstacle obstacle) {
                switch (obstacle.getType()) {
                    case BLUE_CONVEYOR_BELT:
                        move(board.getPlayer(i), obstacle.getDirection(), 2);
                        break;
                    case GREEN_CONVEYOR_BELT:
                        move(board.getPlayer(i), obstacle.getDirection(), 1);
                        break;
                    case PUSH_PANEL:
                        //move the player according to its register
                        //The code below is just for now
                        move(board.getPlayer(i), obstacle.getDirection(), 1);
                        break;
                    case BOARD_LASER:
                        break;
                    case GEAR:
                        break;
                }
            }
        }
    }
}