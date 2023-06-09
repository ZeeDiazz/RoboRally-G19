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
package dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller;

import com.sun.javafx.scene.control.InputField;
import com.sun.javafx.scene.control.IntegerField;
import com.sun.jdi.IntegerValue;
import dk.dtu.compute.se.pisd.roborally.online.Client;
import dk.dtu.compute.se.pisd.roborally.online.RoboRally;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.RoboRally;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.JSONTransformer;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import jakarta.annotation.Resource;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 * This class is responsible for the users interaction with; staring new game, saving game, load game, stop game
 * and exit game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */

public class AppController implements Observer, GameFinishedListener {

    private Client client = new Client(ResourceLocation.baseLocation);

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private static GameController gameController;

    private static JSONTransformer jsonTransformer;
    @FXML
    FileChooser fileChooser = new FileChooser();


    /**
     * @param roboRally The Roborally game being played
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Loads up a game that was loaded from a jsonFile
     *
     * @param gameController GameController which was loaded from a jsonFile
     * @author Zigalow
     */
    protected void makeLoadedGame(GameController gameController) {


        jsonTransformer = new JSONTransformer(gameController);

        AppController.gameController = gameController;

        // Registers this class as listener in the GameController class

        gameController.setGameFinishedListener(this);

        roboRally.createBoardView(gameController);


    }

    // Uses LocalPlayer for now...

    /**
     * Creates a game. When the game is made, a GameController is then made with the game,
     * which is used when creating the view of the RoboRally game
     *
     * @param board       The used for the game
     * @param playerCount The amount of players who will be playing the game
     * @param offlineGame Is true, if an offline game should be made,
     *                    and false if an online game should be made
     * @author Zigalow, Zaid & Daniel
     */
    protected void makeGame(Board board, int playerCount, boolean offlineGame) {
        // Zigalow {
        Game game;

        if (offlineGame) {
            game = new LocalGame(board);


            // Zigalow }

            for (int i = 0; i < playerCount; i++) {
                Player player = new LocalPlayer(game, PLAYER_COLORS.get(i), "Player " + (i + 1));
                Space startingSpace = board.getSpace(i % board.width, i);
                player.robot.setSpace(startingSpace);
                player.robot.setRebootPosition(startingSpace.position);
                game.addPlayer(player);

            }
            gameController = new GameController(game);

        } else {

            // Zaid & Zigalow {

            // Create the custom dialog.
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Choose game ID");
            dialog.setHeaderText("Please enter your preferred game ID");
            dialog.setContentText("Press skip if you don't prefer a specific game Id");

            // Set the button types.
            ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            ButtonType skipButtonType = new ButtonType("Skip", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, skipButtonType);

            // Create the integer input field.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField integerField = new TextField();
            integerField.setPromptText("Enter an integer");

            grid.add(new Label("Integer:"), 0, 0);
            grid.add(integerField, 1, 0);

            // Enable/Disable submit button depending on whether an integer was entered.
            Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
            submitButton.setDisable(true);

            // Do some validation (using the Java 8 lambda syntax).
            integerField.textProperty().addListener((observable, oldValue, newValue) -> {
                submitButton.setDisable(!isValidInteger(newValue));
            });


            dialog.getDialogPane().setContent(grid);

            // Request focus on the integer field by default.


            // Convert the result to an integer when the submit button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == submitButtonType) {
                    return Integer.parseInt(integerField.getText());
                } else {
                    return -1;
                }
            });

            Optional<Integer> result = dialog.showAndWait();

            result.ifPresent(integer -> {
                System.out.println("Entered Integer: " + integer);
            });


            int gameId = result.get();

            // Zaid & Zigalow }

            try {
                client.joinGame(gameId);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            while (!client.gameIsReady()) {
                // wait
            }
            game = client.getGame();
            

            // TODO: 06-06-2023 Thread waiting for players
            /*
            Alert waitingForPlayers = new Alert(AlertType.INFORMATION);
            waitingForPlayers.setTitle("Missing players");
            waitingForPlayers.setContentText("Please wait for the remaining players to join");
            waitingForPlayers.setHeaderText("The game is missing " + (playerCount - game.getPlayerCount()) + " more players to start the game");
            waitingForPlayers.getButtonTypes().clear();


            Platform.runLater(() -> waitingForPlayers.show());


            int currentNumberOfPlayers = game.getPlayerCount();
            while (!game.canStartGame()) {
                if (game.getPlayerCount() != currentNumberOfPlayers) {
                    currentNumberOfPlayers = game.getPlayerCount();
                    int remainingPlayers = playerCount - currentNumberOfPlayers;
                    // Update the alert text on the JavaFX application thread
                    Platform.runLater(() -> waitingForPlayers.setHeaderText("The game is missing " + remainingPlayers + " to start the game"));
                }
            }

            ButtonType startGameButton = new ButtonType("Start Game");
            Alert startGame = new Alert(AlertType.INFORMATION, "", startGameButton);
            startGame.setTitle("Start game");
            startGame.setHeaderText("The game can now be started");
            startGame.setContentText("Press the button to start playing");
            startGame.showAndWait();


            gameController = new GameController(game);*/
        }
        // Zigalow {
        jsonTransformer = new JSONTransformer(gameController);
        // Zigalow}

        gameController.startProgrammingPhase();

        // Registers the event in the GameController class
        // Zigalow {
        gameController.setGameFinishedListener(this);
        // Zigalow }

        roboRally.createBoardView(gameController);
    }

    /**
     * This method firstly creates a dialog dropbox choice dialog with options for numbers of players.
     * Then creates an empty board with the required number of players(including the view)
     * and starts the programming phase
     *
     * @author Zigalow, Zaid
     */
    public void newGame() throws FileNotFoundException {
        // Zigalow {
        ButtonType onlineButton = new ButtonType("Online");
        ButtonType offlineButton = new ButtonType("Offline");

        Alert gameType = new Alert(AlertType.CONFIRMATION, "", onlineButton, offlineButton);

        gameType.setTitle("Choose Game Mode");
        gameType.setHeaderText("Do you wish to play online or offline?");

        Optional<ButtonType> gameMode = gameType.showAndWait();
        // Zigalow }


        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // ZeeDiazz (Zaid) {
            ButtonType map1Button = new ButtonType("Risky Crossing");
            ButtonType map2Button = new ButtonType("Dizzy Highway");

            Alert selectMap = new Alert(Alert.AlertType.CONFIRMATION, "", map1Button, map2Button);
            selectMap.setTitle("Select a map");
            selectMap.setContentText("Choose the map you want to play:");

            Optional<ButtonType> mapResult = selectMap.showAndWait();
            Board board;

            if (mapResult.get() == map1Button) {
                board = MapMaker.makeJsonRiskyCrossing();
            } else {
                board = MapMaker.makeJsonDizzyHighway();
            }
            // ZeeDiazz (Zaid) }

            int playerCount = result.get();

            // Zigalow {
            if (gameMode.get() == offlineButton) {
                makeGame(board, playerCount, true);
            } else {
                makeGame(board, playerCount, false);
            }
            // Zigalow }

        }
    }

    /**
     * Saves the game to a json file. The player chooses where the file should be located on the local computer
     *
     * @author Zigalow
     */

    @FXML
    public void saveGame() {
        if (GameController.game instanceof LocalGame) {
            fileChooser.setInitialDirectory(new File(".")); // Sets directory to project folder

            fileChooser.setTitle("Save Game"); // Description for action
            fileChooser.setInitialFileName("RoboRally_SaveFile"); // Initial name of saveFile
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json")); // Can only be saved as a json file type
            File file = fileChooser.showSaveDialog(null);


            if (file != null) {
                try {
                    file.createNewFile();
                    // Saves to Json-file
                    JSONTransformer.saveBoard(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // If the user opts out of saving
            } else {
                return;
            }


            fileChooser.setInitialDirectory(file.getParentFile()); // Remembers the directory of the last chosen directory
        } else {
            
          /*  if()
            
            File file = new File();

*/
        }
    }

    /**
     * Loads a game from a json file. If the file can't be loading correctly,
     * the player will get an alert saying that the file couldn't be loaded properly
     * It then returns back to the menu
     *
     * @author Zigalow
     */
    @FXML
    public void loadGame() {


        fileChooser.setInitialDirectory(new File(".")); // Sets directory to project folder


        fileChooser.setTitle("Load Game"); // Description for action
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json")); // Can only be load as a json file type
        File file = fileChooser.showOpenDialog(null);


        if (file == null || !file.isFile()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File could not be loaded");
            alert.setContentText("There was a problem with loading the given file");
            alert.showAndWait();
            return;
        }
        GameController gameController1 = null;

        try {
            gameController1 = JSONTransformer.loadBoard(file);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File could not be loaded");
            alert.setContentText("There was a problem with loading the given file");
            alert.showAndWait();
            return;
        }


        makeLoadedGame(gameController1);


        fileChooser.setInitialDirectory(file.getParentFile()); // Remembers the directory of the last chosen directory
    }


    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            // saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * This method alerts the player with a comfirmation to ensure that the player wants to exit the application.
     * It does this through a button pop up, that awaits confirmation from the player.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }


        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * This method checks if the game is running
     *
     * @return True if the current state/instance of game controller is not NULL.
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    /**
     * Stops the game, when an onGameFinished-event occurs,
     * which is when the finishGame() method in the GameController is run
     *
     * @author Zigalow
     */
    @Override
    public void onGameFinished() {
        stopGame();
    }

    private boolean isValidInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
