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

import com.sun.javafx.fxml.builder.JavaFXSceneBuilder;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.RoboRally;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import javafx.application.Platform;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * ...
 * This class is responsible for the users interaction with; staring new game, saving game, load game, stop game
 * and exit game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */

public class AppController implements Observer, GameFinishedListener {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private static GameController gameController;

// --Commented out by Inspection START (04-06-2023 22:42):
//    @FXML
//    FileChooser fileChooser = new FileChooser();
// --Commented out by Inspection STOP (04-06-2023 22:42)

    /**
     * @param roboRally The Roborally game being played
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    // Uses LocalPlayer for now...
    protected void makeGame(Board board, boolean hasCards, int playerCount, boolean offlineGame) {

        Game game;

        if (offlineGame) {
            game = new LocalGame(board);
            for (int i = 0; i < playerCount; i++) {
                Player player = new LocalPlayer(game, PLAYER_COLORS.get(i), "Player " + (i + 1));
                Space startingSpace = board.getSpace(i % board.width, i);
                player.robot.setSpace(startingSpace);
                player.robot.setRebootPosition(startingSpace.position);
                game.addPlayer(player);

                gameController = new GameController(game);
            }

        } else {
             // TODO: 06-06-2023 
          /*  game = new OnlineGame(board, playerCount);


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


        gameController.startProgrammingPhase(!hasCards);

        // Registers the event in the GameController class
        gameController.setGameFinishedListener(this);

        roboRally.createBoardView(gameController);
    }

    /**
     * This method firstly creates a dialog dropbox choice dialog with options for numbers of players.
     * Then creates an empty board with the required number of players(including the view)
     * and starts the programming phase
     */
    public void newGame() {

        ButtonType onlineButton = new ButtonType("Online");
        ButtonType offlineButton = new ButtonType("Offline");

        Alert gameType = new Alert(AlertType.CONFIRMATION, "", onlineButton, offlineButton);

        gameType.setTitle("Choose Game Mode");
        gameType.setHeaderText("Do you wish to play online or offline?");

        Optional<ButtonType> gameMode = gameType.showAndWait();


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

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.


            Board board = MapMaker.makeRiskyCrossing();

            int playerCount = result.get();


            if (gameMode.get() == offlineButton) {
                makeGame(board, false, playerCount, true);
            } else {
                makeGame(board, false, playerCount, false);
            }

        }
    }

    public void saveGame() {

    }

    public void loadGame() {
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
     * Stops the game, when a onGameFinished-event occurs
     */
    @Override
    public void onGameFinished() {
        stopGame();
    }
}
