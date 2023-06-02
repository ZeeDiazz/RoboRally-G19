package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;


import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;

public class GameController {

    public static Board board = null;
    private static ExecuteCommands executeCommands;

    public Command currentInteractiveCard;

    public GameController(@NotNull Board board) {
        this.board = board;
        executeCommands = new ExecuteCommands(board);
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * @param options Refers to the command of the interactive card
     * @author Zigalow
     * This method starts the Player Interaction phase
     */


    // XXX: V3
    private void startPlayerInteractionPhase(Command options) {
        this.currentInteractiveCard = options;
        this.board.setPhase(Phase.PLAYER_INTERACTION);
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

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

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NUMBER_OF_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (card.command.isInteractive()) {
                        startPlayerInteractionPhase(command);
                        return;
                    }
                    executeCommands.executeCommand(currentPlayer, command);
                }

            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * @param option The command which the player has chosen to execute
     * @author Zigalow
     * This method is for executing an interactive card, where a player has chosen what command to execute
     * <p>If all the programs was chosen to be executed before the interactive card,
     * they will continue to do so after an option has been chosen</p>
     */
    public void executeCommandOptionAndContinue(Command option) {
        executeCommands.executeCommand(board.getCurrentPlayer(), option);
        this.board.setPhase(Phase.ACTIVATION);
        nextPlayer(this.board.getCurrentPlayer());
        if (!this.board.isStepMode()) {
            this.executePrograms();
        }
    }

    /**
     * Starts the programming phase. If randomCards is true, random cards will be generated for each player
     *
     * @param randomCards True if cards needs to be randomly generated
     */
    // Should probably be game that has the references instead of board
    public void startProgrammingPhase(boolean randomCards) {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayerCount(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NUMBER_OF_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    if (randomCards) {
                        field.setCard(null);
                        field.setVisible(true);
                    }

                }
                for (int j = 0; j < Player.NUMBER_OF_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    if (randomCards) {
                        field.setCard(generateRandomCommandCard());
                    }
                    field.setVisible(true);
                }
            }
        }
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NUMBER_OF_REGISTERS) {
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
            for (int j = 0; j < Player.NUMBER_OF_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }


    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);

        return new CommandCard(commands[random]);
    }

    /**
     * @author ZeeDiazz (Zaid)
     * It checks if a player is on an obstacle, and executes the obstacles action.
     */
    public void obstacleAction() {
        Move[] moves = new Move[board.getPlayerCount()];
        for (int i = 0; i < board.getPlayerCount(); i++) {
            Player player = board.getPlayer(i);
            moves[i] = player.robot.getSpace().endedRegisterOn(player.robot, 0);
        }
        performSimultaneousMoves(moves);
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
            } else {
                validMoves.put(endingPos, move);
            }
        }

        validMoves.values().forEach(move -> performMove(move));
    }

    public static void performMove(Move move) {
        for (Move resultingMove : board.resultingMoves(move)) {
            Robot robot = resultingMove.moving;
            Space endingSpace = board.getSpace(resultingMove.getEndingPosition());
            // If going out of bounds
            if (endingSpace == null) {
                endingSpace = board.getSpace(robot.getRebootPosition());
            }

            robot.setSpace(endingSpace);
            board.getSpace(resultingMove.start).changed();
            endingSpace.changed();
        }
    }


    /**
     * This method relates to all that has to do with passing on the turn to the next player
     * <p>If the last player has executed his/her last command, the programming phase will start</p>
     *
     * @param currentPlayer The current turn's player before the end of a turn
     * @author Zigalow, Daniel, Zaid Sheikh, Felix723
     */

    public void nextPlayer(Player currentPlayer) {
        this.board.increaseMoveCounter();
        // Daniel {
        int currentStep = this.board.getStep();
        int nextPlayerNumber = this.board.getPlayerNumber(currentPlayer) + 1;
        // nextPlayerNumber++;
        if (nextPlayerNumber >= this.board.getPlayerCount()) {
            nextPlayerNumber = 0;
            currentStep++;
            if (currentStep < Player.NUMBER_OF_REGISTERS) {
                makeProgramFieldsVisible(currentStep);
                //ZeeDiazz (Zaid){
                obstacleAction();
                //ZeeDiazz (Zaid)}

                /*
                //Felix723 (Felix Schmidt){
                for (int i = 0; i < board.getPlayerCount(); i++) {
                    Player checkingPlayer = board.getPlayer(i);
                    if (checkingPlayer.getSpace() instanceof CheckPoint checkPoint) {
                        if (!checkPoint.hasPassed(checkingPlayer)) {
                            checkPoint.playerPassed(checkingPlayer);
                        }

                        if (checkingPlayer.checkpointGoal == board.getCheckpointCount()) {
                            checkingPlayer.setColor("purple");
                        }
                    }
                }
                //Felix723 (Felix Schmidt)}
                */

                board.setStep(currentStep);
            } else {
                startProgrammingPhase(true);
            }


            // Daniel }

        }
        this.board.setCurrentPlayer(this.board.getPlayer(nextPlayerNumber));
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
     * @param space The space which the player's robot is going to be moved to
     * @author Zigalow
     * This method makes it possible to click on a space, and make the current player's robot move to that space
     */

    /**
     * @param space The space which is checked whether it's being occupied by a robot
     * @return Returns true if there is another robot on the space received as parameter
     * @author Zigalow
     * This method is for checking whether a space is being occupied by a robot
     */

    public boolean spaceIsOccupied(Space space) {
        return space.getRobot() != null;
    }

    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        Player currentPlayer = board.getCurrentPlayer();

        if (spaceIsOccupied(space)) {
            return;
        } else {
            currentPlayer.robot.setSpace(space);
        }
        nextPlayer(currentPlayer);
    }

}


// Needs board
// public ExecuteCommands ()
