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

import java.util.ArrayList;
import java.util.Hashtable;

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

        for (int i = 0; i < board.getPlayerCount(); i++) {
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
            for (int i = 0; i < board.getPlayerCount(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayerCount(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }


    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }


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
        performMove(Move.fromPlayer(player, 1));
    }

    // From 1.4.0
    /*public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }*/
    
    // From 1.4.0
    /*void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null){
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

}*/
    
    
    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        performMove(Move.fromPlayer(player, 2));
    }

    private void performSimultaneousMoves(Move... moves) {
        Hashtable<Position, Move> validMoves = new Hashtable<>();
        ArrayList<Position> colliding = new ArrayList<>();
        for (Move move : moves) {
            if (move == null) {
                continue;
            }

            Position endingPos = move.getEndingPosition();
            if (colliding.contains(endingPos)) {
                continue;
            }

            if (validMoves.containsKey(endingPos)) {
                colliding.add(endingPos);
                validMoves.remove(endingPos);
            }
            else {
                validMoves.put(endingPos, move);
            }
        }

        validMoves.values().forEach(this::performMove);
    }

    private void performMove(Move move) {
        Player player = move.Moving;
        Heading direction = move.Direction;

        Space currentSpace = player.getSpace();

        for (int i = 0; i < move.Amount; i++) {
            if (!currentSpace.canMove(direction)) {
                continue;
            }
            currentSpace = player.board.getNeighbour(currentSpace, direction);
            if (currentSpace == null) {
                break;
            }
            if (currentSpace.hasPlayer()) {
                Move otherPlayerMove = new Move(currentSpace.Position, direction, 1, currentSpace.getPlayer());
                performMove(otherPlayerMove);
            }
        }

        if (currentSpace == null) {
            player.reboot();
        }
        else {
            player.setSpace(currentSpace);
        }

        // Is this still required?
        /*
        if (spaceIsOccupied(currentSpace)) {
            return;
        }
         */
    }

    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {
        Heading playerDirection = player.getHeading();
        Heading newDirection = Heading.turnRight(playerDirection);

        player.setHeading(newDirection);
    }

    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {
        Heading playerDirection = player.getHeading();
        Heading newDirection = Heading.turnLeft(playerDirection);

        player.setHeading(newDirection);
    }

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
     * @author Zigalow, Daniel, Zaid Sheikh, Felix723
     * This method relates to all that has to do with passing on the turn to the next player
     * <p>If the last player has executed his/her last command, the programming phase will start</p>
     *
     * @param currentPlayer The current turn's player before the end of a turn
     *
     */

    public void nextPlayer(Player currentPlayer) {
        this.board.increaseMoveCounter();
        // Daniel {
        int currentStep = this.board.getStep();
        int nextPlayerNumber = this.board.getPlayerNumber(currentPlayer);
        nextPlayerNumber++;
        if (nextPlayerNumber >= this.board.getPlayerCount()) {
            nextPlayerNumber = 0;
            currentStep++;
            if (currentStep < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(currentStep);
                //ZeeDiazz (Zaid){
                obstacleAction(currentPlayer);
                //ZeeDiazz (Zaid)}

                //Felix723 (Felix Schmidt){
                for (int i = 0; i < board.getPlayerCount(); i++) {
                    Player checkingPlayer = board.getPlayer(i);
                    if (checkingPlayer.getSpace() instanceof CheckPoint checkPoint) {
                        if (!checkPoint.hasPassed(checkingPlayer)) {
                            checkPoint.playerPassed(checkingPlayer);
                        }

                        if (checkingPlayer.checkpointGoal == Board.checkpointCount) {
                            checkingPlayer.setColor("purple");
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
        Move[] moves = new Move[board.getPlayerCount()];
        for (int i = 0; i < board.getPlayerCount(); i++) {
            if (board.getPlayer(i).getSpace() instanceof Obstacle obstacle) {
                switch (obstacle.getType()) {
                    case BLUE_CONVEYOR_BELT:
                        moves[i] = new Move(obstacle.Position, obstacle.getDirection(), 2, board.getPlayer(i));
                        break;
                    case GREEN_CONVEYOR_BELT:
                        moves[i] = new Move(obstacle.Position, obstacle.getDirection(), 1, board.getPlayer(i));
                        break;
                    case PUSH_PANEL:
                        //move the player according to its register
                        //The code below is just for now
                        moves[i] = new Move(obstacle.Position, obstacle.getDirection(), 1, board.getPlayer(i));
                        break;
                    case BOARD_LASER:
                        break;
                    case GEAR:
                        break;
                }
            }
        }
        performSimultaneousMoves(moves);
    }
}