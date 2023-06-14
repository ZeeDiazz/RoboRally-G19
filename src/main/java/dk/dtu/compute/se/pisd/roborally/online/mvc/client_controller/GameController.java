package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.CheckPointSpace;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view.ProgrammingObserver;
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
    private ProgrammingObserver programmingObserver;

    public static Board board = null;
    public static Game game = null;
    /***
     * For executing the different commands
     * Zigalow
     */
    private static CommandExecuter commandExecution;

    // TODO [Javadoc]: 13-06-2023


    public GameController(@NotNull Game game) {
        this.game = game;
        commandExecution = new CommandExecuter();
        this.game.priorityAntennaSpace = game.board.getPriorityAntennaSpace();
    }

    public GameController(@NotNull Board board) {
        this.board = board;
        commandExecution = new CommandExecuter();
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
        this.game.currentInteractiveCard = options;
        this.game.setPhase(Phase.PLAYER_INTERACTION);
    }

    // TODO [Javadoc]: 13-06-2023

    public void executeStep() {
        game.setStepMode(true);
        continuePrograms();
    }
    // TODO [Javadoc]: 13-06-2023

    private void continuePrograms() {
        do {
            executeNextStep();
            if (this.game.getPhase() != Phase.PLAYER_INTERACTION) {
                nextPlayer();
            } else {
                return;
            }
        } while (game.getPhase() == Phase.ACTIVATION && !game.isStepMode());
    }

    // TODO [Javadoc]: 13-06-2023

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
        nextPlayer();
        if (!this.game.isStepMode()) {
            this.executePrograms();
        }
    }


    public void startProgrammingPhase() {
        startedProgramming();

        game.setPhase(Phase.PROGRAMMING);
        game.setCurrentPlayer(game.getPlayer(0));
        game.setStep(0);


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
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        game.setPhase(Phase.ACTIVATION);
        game.setStep(0);
        updatePrioritisedRobotsForAllPlayers();
        game.setCurrentPlayer(game.prioritisedPlayers.remove(0));

        finishedProgramming();
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
     * It checks if a player is on an obstacle, and executes the obstacles' action.
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
    // TODO [Javadoc]: 13-06-2023

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

    // TODO [Javadoc]: 13-06-2023

    /**
     *
     * @param move
     * @author Daniel
     */
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
     * @author Zigalow, Daniel, Zaid Sheikh
     */

    public void nextPlayer() {
        // Zigalow {
        updatePrioritisedRobotsCurrentList();
        // Zigalow }
        this.game.increaseMoveCounter();
        // Daniel {
        int currentStep = this.game.getStep();
        // Zigalow {
        if (game.prioritisedPlayers.isEmpty()) {
            // Zigalow }
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
        // Zigalow {
        if (!game.prioritisedPlayers.isEmpty()) {
            this.game.setCurrentPlayer(game.prioritisedPlayers.remove(0));
        } else {
            updatePrioritisedRobotsForAllPlayers();
            this.game.setCurrentPlayer(game.prioritisedPlayers.remove(0));
        }
        // Zigalow }
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
     * This method is for checking whether a space is being occupied by a robot
     *
     * @param space The space which is checked whether it's being occupied by a robot
     * @return Returns true if there is another robot on the space received as parameter
     * @author Zigalow
     */

    public boolean spaceIsOccupied(Space space) {
        return space.getRobot() != null;
    }

    /**
     * This method makes it possible to click on a space, and make the current player's robot move to that space
     *
     * @param space The space which the player's robot is going to be moved to
     * @author Zigalow
     */
    public void moveCurrentPlayerToSpaceWithMouseClick(@NotNull Space space) {
        Player currentPlayer = game.getCurrentPlayer();

        if (spaceIsOccupied(space)) {
            return;
        } else {
            currentPlayer.robot.setSpace(space);
        }
        nextPlayer();
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

    /**
     * Sets the gameController to register the argument object as a listener
     * That way, when the game finishes, the class will notify its listener that the game has finished
     *
     * @param gameFinishedListener
     * @author Zigalow
     */
    public void setGameFinishedListener(GameFinishedListener gameFinishedListener) {
        this.gameFinishedListener = gameFinishedListener;
    }

    public void setProgrammingObserver(ProgrammingObserver observer) {
        this.programmingObserver = observer;
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

        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();

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

        return gameController;
    }

    /**
     * Updates the game's prioritedPlayers attribute based on the current list's robots' currentPosition
     *
     * @author Zigalow
     */

    private void updatePrioritisedRobotsCurrentList() {

        List<Player> remainingPlayers = new ArrayList<>(game.prioritisedPlayers);
        List<Robot> remainingRobots = new ArrayList<>();


        for (Player player : remainingPlayers) {
            remainingRobots.add(player.getRobot());
        }

        remainingRobots = getPriority(remainingRobots);


        remainingPlayers.clear();
        for (Robot robot : remainingRobots) {
            remainingPlayers.add(robot.getOwner());
        }

        game.prioritisedPlayers = remainingPlayers;

    }

    /**
     * Updates the game's prioritedPlayers attribute, so that the attribute has a new prioritised list
     * based on all robots' currentPosition
     *
     * @author Zigalow
     */
    private void updatePrioritisedRobotsForAllPlayers() {
        List<Robot> robotsInPriority = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            robotsInPriority.add(player.getRobot());
        }

        robotsInPriority = getPriority(robotsInPriority);

        List<Player> prioritisedPlayers = new ArrayList<>();

        for (Robot robot : robotsInPriority) {
            prioritisedPlayers.add(robot.getOwner());
        }

        game.prioritisedPlayers = prioritisedPlayers;
    }

    /**
     * This method is for creating and getting a list
     * of robots sorted by priority based on RoboRally rules
     *
     * @param robots
     * @return prioritylist, a list of player sorted  by priority
     * @author Felix723
     */
    private List<Robot> getPriority(List<Robot> robots) {
        List<Robot> tied = new ArrayList<>();
        List<Robot> priority = new ArrayList<>();
        int previousPlayerDistance = -1;
        int robotsSize = robots.size();
        robots.sort((a, b) -> (getDistanceTo(a) - getDistanceTo(b)));

        for (int i = 0; i < robotsSize; i++) {
            Robot current = robots.remove(0);

            if (getDistanceTo(current) != previousPlayerDistance) {
                tied.sort((a, b) -> Double.compare(getAngle(a), getAngle(b)));
                priority.addAll(tied);
                tied.clear();
            }
            previousPlayerDistance = getDistanceTo(current);
            tied.add(current);
        }
        tied.sort((a, b) -> Double.compare(getAngle(a), getAngle(b)));
        priority.addAll(tied);
        return priority;
    }

    /**
     * Returns the Manhattan distance between this antenna and the given robot's space.
     *
     * @param robot the robot whose distance to this antenna is to be calculated
     * @return the Manhattan distance between this antenna and the robot's space
     */
    private int getDistanceTo(Robot robot) {
        int xAntenna = game.priorityAntennaSpace.position.X;
        int yAntenna = game.priorityAntennaSpace.position.Y;
        int xPlayer = robot.getSpace().position.X;
        int yPlayer = robot.getSpace().position.Y;
        return Math.abs(xPlayer - xAntenna) + Math.abs(yPlayer - yAntenna);
    }

    /**
     * Returns the angle between this antenna and the given robot's space in radians.
     *
     * @param robot the robot whose angle to this antenna is to be calculated
     * @return the angle between this antenna and the robot's space in radians
     */
    private double getAngle(Robot robot) {
        // (x1,y1) = priorityantenna
        // (x2,y2) = robot

        int xAntenna = game.priorityAntennaSpace.position.X;
        int yAntenna = game.priorityAntennaSpace.position.Y;

        int xPlayer = robot.getSpace().position.X;
        int yPlayer = robot.getSpace().position.Y;
        if (yPlayer == yAntenna) {
            yPlayer++;
        }
        int xDifference = xPlayer - xAntenna;
        int yDifference = yPlayer - yAntenna;

        //double cos = (xAntenna - xPlayer)/(yAntenna -yPlayer);
        //return Math.acos(cos);
        return Math.atan2(yDifference, xDifference);
    }

    public void finishedProgramming() {
        if (programmingObserver != null) {
            programmingObserver.finished();
        }
    }

    public void startedProgramming() {
        if (programmingObserver != null) {
            programmingObserver.started();
        }
    }
}
