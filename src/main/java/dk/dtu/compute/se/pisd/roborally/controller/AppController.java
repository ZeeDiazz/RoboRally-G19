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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.Transformer;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.spaces.Space;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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


public class AppController implements Observer {


    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private static Transformer transformer;
    private static GameController gameController;

    @FXML
    FileChooser fileChooser = new FileChooser();

    /**
     * @param roboRally The Roborally game being played
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    protected void makeGame(Board board, boolean hasCards) {
        gameController = new GameController(board);

        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase(!hasCards);

        // Gives transformer the currentGameController
        transformer = new Transformer(gameController);

        roboRally.createBoardView(gameController);
    }

    /**
     * This method firstly creates a dialog dropbox choice dialog with options for numbers of players.
     * Then creates an empty board with the required number of players(including the view)
     * and starts the programming phase
     */
    public void newGame() {
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

            Board board = MapMaker.makeDizzyHighway();
            int playerCount = result.get();
            for (int i = 0; i < playerCount; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                Space startingSpace = board.getSpace(i % board.width, i);
                player.setSpace(startingSpace);
                player.setRebootPosition(startingSpace.position);
                board.addPlayer(player);
            }

            makeGame(board, false);
            /*
            if (true)
            return;

            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                Space startingSpace = board.getSpace(i % board.width, i);
                player.setSpace(startingSpace);
                player.setRebootPosition(startingSpace.position);
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            // Gives transformer the currentGameController
            transformer = new Transformer(gameController);

            roboRally.createBoardView(gameController);
             */
        }
    }


    /**
     * Saves the game to a json file. The player chooses where the file should be located on the local computer
     * @author Zigalow
     */


    @FXML
    public void saveGame() {

        fileChooser.setInitialDirectory(new File(".")); // Sets directory to project folder


        fileChooser.setTitle("Save Game"); // Description for action
        fileChooser.setInitialFileName("mysave"); // Initial name of saveFile
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json")); // Can only be saved as a json file type
        File file = fileChooser.showSaveDialog(null);


        if (file != null) {
            try {
                file.createNewFile();
                // Saves to Json-file
                Transformer.saveBoard(gameController.board, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // If the user opts out of saving
        } else {
            return;
        }


        fileChooser.setInitialDirectory(file.getParentFile()); // Remembers the directory of the last chosen directory

    }


    /**
     * Loads a game from a json file. If the file can't be loading correctly,
     * the player will get an alert saying that the file couldn't be load properly
     * It then returns back to the menu 
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
        Board board;

        try {
            board = Transformer.loadBoard(file);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File could not be loaded");
            alert.setContentText("There was a problem with loading the given file");
            alert.showAndWait();
            return;
        }

        makeGame(board, true);

        /*
        // New approach for loading game. This sets the current GameController, to the one loaded in the transformer
        //gameController = transformer.getCurrentGameController();

        gameController = new GameController(board);

        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase(false);

        roboRally.createBoardView(gameController);

        // Provided error pop-up if there was a problem with loading the file


        // If the user opts out of loading
    } else {
        return;
    }


    */
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
}
