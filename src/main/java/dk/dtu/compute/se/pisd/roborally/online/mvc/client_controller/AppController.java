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

import dk.dtu.compute.se.pisd.roborally.online.Client;
import dk.dtu.compute.se.pisd.roborally.online.RoboRally;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.*;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.spaces.Space;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.JSONTransformer;
import dk.dtu.compute.se.pisd.roborally.restful.ResourceLocation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

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

    private Game game;

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

    private void createBoardView() {
        gameController = new GameController(game);
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

        if (offlineGame) {
            game = new LocalGame(board);
            // Zigalow }

            for (int i = 0; i < playerCount; i++) {
                game.addPlayer(new LocalPlayer(game, PLAYER_COLORS.get(i), "Player " + (i + 1)));
            }
        } else {

            // Zaid & Zigalow {
            int gameId;
            try {
                gameId = chooseGameIdCreateGame();
                client.createGame(gameId, playerCount, board.boardName);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                Alert waiter = new Alert(AlertType.CONFIRMATION, "Do you want to start the game?", ButtonType.OK);
                waiter.showAndWait();

                try {
                    if (client.canStartGame()) {
                        break;
                    }
                } catch (URISyntaxException | IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Alert cannotStartGame = new Alert(AlertType.ERROR, "Cannot start game right now", ButtonType.OK);
                cannotStartGame.showAndWait();
            }

            System.out.println("Starting the game");
            client.startGame();
            game = client.getGame();
        }

        for (int i = 0; i < game.getPlayerCount(); i++) {
            Space startingSpace = game.board.getSpace(Board.spawnPositions.get(i).X, Board.spawnPositions.get(i).Y);

            Player player = game.getPlayer(i);
            player.robot.setSpace(startingSpace);
            player.robot.setRebootPosition(startingSpace.position);
        }
        createBoardView();
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

        if (gameMode.get() == onlineButton) {
            ButtonType listAllGames = new ButtonType("See game list");
            ButtonType getGameInfo = new ButtonType("Get game info");
            ButtonType playGame = new ButtonType("Play game");

            ButtonType option;

            Alert selectOption = new Alert(AlertType.CONFIRMATION, "", playGame, listAllGames, getGameInfo);


            selectOption.setTitle("Choose option");
            selectOption.setHeaderText("What would you like to do?");
            selectOption.setContentText("Select the option you would like to perform");


            do {
                option = selectOption.showAndWait().get();

                // todo - getGameInfo and listAllGames usages
                if (option == getGameInfo) {
                    getGameInfo(chooseGameIdWindow());
                } else if (option == listAllGames) {
                    listAllGames();
                }

            } while (option != playGame);

            ButtonType createGame = new ButtonType("Create new game");
            ButtonType joinGame = new ButtonType("Join game");


            Alert gameOptions = new Alert(AlertType.CONFIRMATION, "", createGame, joinGame);

            gameOptions.setTitle("Choose game option");
            gameOptions.setHeaderText("How would you like to play?");
            gameOptions.setContentText("Select the option you would like to perform");

            // TODO fix the loop, so it can handle not having to choose the amount of players and so on
            do {
                option = gameOptions.showAndWait().get();

                Alert errorAlert = new Alert(AlertType.INFORMATION);
                int gameId;
                // todo - load game usages
                if (option == joinGame) {
                    try {
                        gameId = chooseGameIdWindow();

                        // If player closes window
                        if (gameId == -2) {
                            continue;
                        }
                        int playerId = client.joinGame(gameId);


                        if (playerId == -1) {
                            errorAlert.setTitle("Game doesn't exist");
                            errorAlert.setHeaderText("Game doesn't exist");
                            errorAlert.setContentText("There isn't any available game, that matches the entered game Id of " + gameId);
                            continue;
                        } else if (playerId == 0) {
                            errorAlert.setTitle("Game is full");
                            errorAlert.setHeaderText("Game is full");
                            errorAlert.setContentText("The game of game ID " + gameId + " is already filled up");
                            continue;
                        }

                        while (!client.gameIsReady()) {
                            Thread.sleep(10);
                        }
                        game = client.getGame();
                        createBoardView();
                        return;


                    } catch (IOException | InterruptedException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            } while (option != createGame);
        }


        // Creates game

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
            ButtonType map1Button = new ButtonType("RiskyCrossing");
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

            makeGame(board, playerCount, gameMode.get() == offlineButton);
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
            //TODO:
            try {
                Alert alert = new Alert(AlertType.INFORMATION);

                if (client.saveGame()) {
                    alert.setTitle("Saved succesfully");
                    alert.setHeaderText("The game was succesfully saved");
                    alert.showAndWait();
                } else {
                    alert.setTitle("Save failed");
                    alert.setHeaderText("The game couldn't be saved");
                    alert.setContentText("There was a problem with connecting to the server");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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

        // Zigalow {
        ButtonType onlineButton = new ButtonType("Online");
        ButtonType offlineButton = new ButtonType("Offline");

        Alert gameType = new Alert(AlertType.CONFIRMATION, "", onlineButton, offlineButton);

        gameType.setTitle("Choose Game Mode");
        gameType.setHeaderText("Do you wish to play online or offline?");

        Optional<ButtonType> gameMode = gameType.showAndWait();
        // Zigalow }

        if (gameMode.get() == onlineButton) {
            do {
                Alert errorAlert = new Alert(AlertType.ERROR);
                int gameId = chooseGameIdWindow();
                // Closes the window
                if (gameId == -2) {
                    return;
                }
                try {
                    Game initialGame = client.loadGame(gameId);
                    if (initialGame.getGameId() == -1) {
                        errorAlert.setTitle("Game doesn't exist");
                        errorAlert.setHeaderText("Game doesn't exist");
                        errorAlert.setContentText("There isn't any available game, that matches the entered game Id of " + gameId);
                        continue;
                    } else if (initialGame.getGameId() == 0) {
                        errorAlert.setTitle("Game is full");
                        errorAlert.setHeaderText("Game is full");
                        errorAlert.setContentText("The game of game ID " + gameId + " is already filled up");
                        continue;
                    } else if (initialGame.getGameId() > 0) {
                        // TODO: 11-06-2023 - waiting for players usage 
                        game = initialGame;
                        //waitingForPlayers();
                        createBoardView();
                        return;
                    }

                } catch (URISyntaxException | InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            } while (true);


        } else if (gameMode.get() == offlineButton) {


            fileChooser.setInitialDirectory(new File(".")); // Sets directory to project folder


            fileChooser.setTitle("Load Game"); // Description for action
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json file", "*.json")); // Can only be load as a json file type
            File file = fileChooser.showOpenDialog(null);


            if (file == null || !file.isFile()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("File could not be loaded");
                alert.setHeaderText("Invalid or no file chosen");
                alert.showAndWait();
                return;
            }
            GameController gameController1 = null;

            try {
                gameController1 = JSONTransformer.loadBoard(file);
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("File could not be loaded");
                alert.setHeaderText("There was a problem with loading the given file");
                alert.setContentText("Is the json file you're trying to load made from this game?");
                alert.showAndWait();
                return;
            }


            makeLoadedGame(gameController1);


            fileChooser.setInitialDirectory(file.getParentFile()); // Remembers the directory of the last chosen directory
        }

    }

    public void deleteSavedGame() {
        int gameId;

        try {
            gameId = chooseGameIdDeleteSavedGame();
            client.deleteSavedGame(gameId);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.OK);
        alert.setTitle("Delete Game");
        alert.setHeaderText("Game deleted");
        alert.setContentText("Successfully deleted game with gameId: " + gameId);

        alert.showAndWait();
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

    private int chooseGameIdDeleteSavedGame() {

        // Create the dialog for choosing a gameId
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Choose game ID");
        dialog.setHeaderText("Please enter your preferred game ID");


        // add the Buttons Submit & skip
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType);

        //
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 150, 10, 10)); //10 height & 150 width


        TextField integerField = new TextField();
        integerField.setPromptText("Enter a positive integer"); //a text prompt telling the player to write an int

        grid.add(new Label("Game ID:"), 0, 0);
        grid.add(integerField, 1, 0);

        // Enable/Disable submit button depending on whether an integer was entered.
        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // Do some validation.
        integerField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(!isValidInteger(newValue));
        });


        dialog.getDialogPane().setContent(grid);

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

        return result.get();
    }

    private int chooseGameIdCreateGame() {

        // Create the dialog for choosing a gameId
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Choose game ID");
        dialog.setHeaderText("Please enter your preferred game ID");


        // add the Buttons Submit & skip
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        // Call it; "Make it random" instead
        ButtonType skipButtonType = new ButtonType("I don't prefer a Game ID", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, skipButtonType);

        //
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 150, 10, 10)); //10 height & 150 width


        TextField integerField = new TextField();
        integerField.setPromptText("Enter a positive integer"); //a text prompt telling the player to write an int

        grid.add(new Label("Game ID:"), 0, 0);
        grid.add(integerField, 1, 0);

        // Enable/Disable submit button depending on whether an integer was entered.
        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // Do some validation.
        integerField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(!isValidInteger(newValue));
        });


        dialog.getDialogPane().setContent(grid);

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

        return result.get();
    }

    private int chooseGameIdWindow() {

        // Create the dialog for choosing a gameId
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Choose game ID");
        dialog.setHeaderText("Please enter your preferred game ID");


        // add the Buttons Submit & skip
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType);

        //
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 150, 10, 10)); //10 height & 150 width


        TextField integerField = new TextField();
        integerField.setPromptText("Enter a positive integer"); //a text prompt telling the player to write an int

        grid.add(new Label("Game ID:"), 0, 0);
        grid.add(integerField, 1, 0);

        // Enable/Disable submit button depending on whether an integer was entered.
        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // Do some validation.
        integerField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(!isValidInteger(newValue));
        });


        dialog.getDialogPane().setContent(grid);

        // Convert the result to an integer when the submit button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return Integer.parseInt(integerField.getText());
            } else {
                return -2;
            }
        });
        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(integer -> {
            System.out.println("Entered Integer: " + integer);
        });

        return result.get();
    }

    private void waitingForPlayers(int gameID) {
        // todo - implement waiting for players

        // Host
        if (game != null) {
            Thread hostIsGameReady = new Thread(new Runnable() {
                int number = 0;

                @Override
                public void run() {
                    while (true) {
                        try {
                            if (client.canStartGame()) {
                                break;
                            }
                            Thread.sleep(1000);
                            System.out.println(number++);
                        } catch (URISyntaxException | IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // todo - set client game to game, that is ready to start, from server. (gets new copy of game from server)

                    Alert alert = new Alert(AlertType.INFORMATION, "", new ButtonType("Start game"));
                    alert.setTitle("Start game");
                    alert.setHeaderText("The game is ready to be started");
                    alert.setContentText("Press start game, to start the game");
                }
            });
        }
        // Not host-client
        else {
            Thread nonHostIsGameReady = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (client.canStartGame()) {
                                break;
                            }
                        } catch (URISyntaxException | IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // todo - set client game to game, that is ready to start, from server. (gets new copy of game from server)
                    Alert alert = new Alert(AlertType.INFORMATION, "", new ButtonType("Start game"));
                    alert.setTitle("Start game");
                    alert.setHeaderText("The game is ready to be started");
                    alert.setContentText("Press start game, to start the game");
                }
            });


        }
    }

    private void getGameInfo(int gameId) {
        // todo - add implementation of getGameInfo
    }

    private void listAllGames() {
        // todo - add implementation of listAllGames
    }

    private boolean isValidInteger(String text) {
        try {

            return Integer.parseInt(text) > 0;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}
