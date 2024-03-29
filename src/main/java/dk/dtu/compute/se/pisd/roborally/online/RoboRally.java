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
package dk.dtu.compute.se.pisd.roborally.online;

import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.AppController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view.BoardView;
import dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view.RoboRallyMenuBar;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ...
 * This class instantiates the AppController then creates a new menu bar and an empty board.
 * It also contains the main method to launch a game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 480;

    private Stage stage;
    private BorderPane boardRoot;
    private RoboRallyMenuBar menuBar;

    private AppController appController;

    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * This method instantiates the AppController then creates a new menu bar and an empty board,
     * sets the stage window and displays it.
     *
     * @param primaryStage is the initial stage of the game
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        AppController appController = new AppController(this);

        // create the primary scene with the a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();
                });
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * This method creates and add view for a new board
     *
     * @param gameController controller of the game
     */
    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    /**
     * Main method to launch a new application.
     * Does not currently work with the current version of JavaFX
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

}