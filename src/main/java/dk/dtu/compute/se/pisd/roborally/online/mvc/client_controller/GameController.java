package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.CheckPointSpace;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.PriorityAntennaSpace;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * ...
 * This class is responsible for the users interaction with:
 * game phases, execution of cards, execution of steps, movement, and who's the current player.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */

public class GameController implements Serializable {

    private GameFinishedListener gameFinishedListener;

    public static Board board = null;
    public static Game game = null;
    private static CommandExecuter commandExecution;
    /**
     * This attribute is relating to the interactive cards. The property of this attribute will be set to the latest interactive card from a register.
     * This is also so that the PlayerView class is able to access the interactive card in question
     *
     * @author Zigalow
     */
    public Command currentInteractiveCard;

    public GameController(@NotNull Game game) {
        this.game = game;
        commandExecution = new CommandExecuter(game.board);


    }

    public GameController(@NotNull Board board) {
        this.board = board;
        commandExecution = new CommandExecuter(board);
    }

    public void executePrograms() {
        game.setStepMode(false);
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
        this.game.setPhase(Phase.PLAYER_INTERACTION);
    }

    public void executeStep() {
        game.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
            if (this.game.getPhase() != Phase.PLAYER_INTERACTION) {
                nextPlayer(game.getCurrentPlayer());
            } else {
                return;
            }
        } while (game.getPhase() == Phase.ACTIVATION && !game.isStepMode());
    }

    private void executeNextStep() {
        Player currentPlayer = game.getCurrentPlayer();
        if (game.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = game.getStep();
            if (step >= 0 && step < Player.NUMBER_OF_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (card.command.isInteractive()) {
                        startPlayerInteractionPhase(command);
                        return;
                    }
                    commandExecution.executeCardCommand(currentPlayer, command);
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
        commandExecution.executeCardCommand(game.getCurrentPlayer(), option);
        this.game.setPhase(Phase.ACTIVATION);
        nextPlayer(this.game.getCurrentPlayer());
        if (!this.game.isStepMode()) {
            this.executePrograms();
        }
    }



    public void startProgrammingPhase() {
        // Felix: This is a temporary fix for the priority antenna{

            game.setPhase(Phase.PROGRAMMING);
            game.setCurrentPlayer(game.getPlayer(0));
            game.setStep(0);// Felix: }

        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NUMBER_OF_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);

                    field.setCard(null);
                    field.setVisible(true);


                }
                for (int j = 0; j < Player.NUMBER_OF_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    public void finishProgrammingPhase() {
        Space priorityAntennaSpace = game.board.getSpace(11, 0);
        List<Robot> robots = null;
        PriorityAntennaSpace priorityAntenna = (PriorityAntennaSpace) priorityAntennaSpace;
        List<Robot> robotsList = new ArrayList<>();
            for (int i = 0; i < game.getPlayerCount(); i++) {
                robotsList.add(game.getPlayer(i).robot);
            }

            robots = priorityAntenna.getPriority(robotsList);
            makeProgramFieldsInvisible();
            game.setPhase(Phase.ACTIVATION);
            game.setCurrentPlayer(robots.get(0).owner);
            game.setStep(0);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NUMBER_OF_REGISTERS) {
            for (int i = 0; i < game.getPlayerCount(); i++) {
                Player player = game.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
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
     * It checks if a player is on an obstacle, and executes the obstacles action.
     *
     * @author ZeeDiazz (Zaid)
     */
    public void obstacleAction() {
        List<Robot> checkRobotsCheckpoints = new ArrayList<>();
        Move[] moves = new Move[game.getPlayerCount()];

        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
            Move move = player.robot.getSpace().endedRegisterOn(player.robot, 0);
            moves[i] = move;

            if (player.robot.getSpace() instanceof CheckPointSpace) {
                checkRobotsCheckpoints.add(player.robot);
            }
        }
        performSimultaneousMoves(moves);

        for (Robot robot : checkRobotsCheckpoints) {
            if (allCheckPointReached(robot)) {
                finishGame(robot);
            }

        }

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
        for (Move resultingMove : game.resultingMoves(move)) {
            Robot robot = resultingMove.moving;
            Space endingSpace = game.board.getSpace(resultingMove.getEndingPosition());
            // If going out of bounds
            if (endingSpace == null) {
                endingSpace = game.board.getSpace(robot.getRebootPosition());
            }

            robot.setSpace(endingSpace);
            game.board.getSpace(resultingMove.start).changed();
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
        this.game.increaseMoveCounter();
        // Daniel {
        int currentStep = this.game.getStep();
        int nextPlayerNumber = this.game.getPlayerNumber(currentPlayer) + 1;
        // nextPlayerNumber++;
        if (nextPlayerNumber >= this.game.getPlayerCount()) {
            nextPlayerNumber = 0;
            currentStep++;
            if (currentStep < Player.NUMBER_OF_REGISTERS) {
                makeProgramFieldsVisible(currentStep);
                //ZeeDiazz (Zaid){
                obstacleAction();
                //ZeeDiazz (Zaid)}

                game.setStep(currentStep);
            } else {
                startProgrammingPhase();
            }
            // Daniel }

        }
        this.game.setCurrentPlayer(this.game.getPlayer(nextPlayerNumber));
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

    /**
     * @param space The space which the player's robot is going to be moved to
     * @author Zigalow
     * This method makes it possible to click on a space, and make the current player's robot move to that space
     */
    public void moveCurrentPlayerToSpaceWithMouseClick(@NotNull Space space) {
        Player currentPlayer = game.getCurrentPlayer();

        if (spaceIsOccupied(space)) {
            return;
        } else {
            currentPlayer.robot.setSpace(space);
        }
        nextPlayer(currentPlayer);
    }

    public boolean allCheckPointReached(Robot robot) {
        return robot.checkpointsReached >= game.board.getCheckpointAmount();
    }

    /**
     * Finishes the game. An alert pops up and informs the players of the winning robot
     * <p>Also triggers the event, onGameFinished(), if a listener is registered</p>
     *
     * @param winningRobot Robot of player who has won the game
     * @author Zigalow
     */
    public void finishGame(Robot winningRobot) {
        ButtonType closeGameButton = new ButtonType("Close game");

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", closeGameButton);
        alert.setTitle("Winner");
        alert.setHeaderText("Congratulations, " + winningRobot.getOwner().getName() + ", on winning the game");
        alert.setContentText("Close this window to exit the game");
        alert.setResizable(true);
        alert.showAndWait();
        

        if (gameFinishedListener != null) {
            gameFinishedListener.onGameFinished();
        }

    }

    public void setGameFinishedListener(GameFinishedListener gameFinishedListener) {
        this.gameFinishedListener = gameFinishedListener;
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("gameType", game.getClass().getSimpleName());
        if (game instanceof OnlineGame) {
            jsonObject.addProperty("numberOfPlayersToStart", ((OnlineGame) game).getNumberOfPlayersToStart());
        }
        // jsonObject.add("board", board.serialize());
        jsonObject.add("game", game.serialize());


        if (currentInteractiveCard != null) {
            jsonObject.addProperty("currentInteractiveCard", currentInteractiveCard.toString());
        }

        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

       /* Board initialBoard = new Board(0, 0);
        initialBoard = (Board) initialBoard.deserialize(jsonObject.get("board"));
*/
        String gameType = jsonObject.getAsJsonPrimitive("gameType").getAsString();


        Game initialGame;
        if (gameType.equals("LocalGame")) {
            initialGame = new LocalGame(new Board(10, 10));
            initialGame = (LocalGame) initialGame.deserialize(jsonObject.get("game"));
        } else {
            initialGame = new OnlineGame(new Board(0, 0), jsonObject.getAsJsonPrimitive("numberOfPlayersToStart").getAsInt());
            initialGame = (OnlineGame) initialGame.deserialize(jsonObject.get("game"));
        }


        GameController gameController = new GameController(initialGame);


        JsonElement commandCard = jsonObject.get("currentInteractiveCard");


        gameController.currentInteractiveCard = commandCard == null ? null : Command.valueOf(commandCard.getAsJsonPrimitive().getAsString());


        return gameController;
    }
}
