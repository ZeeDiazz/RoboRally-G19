package dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.FileNotFoundException;

/**
 * ...
 * This is a class for creating menu bars for RoboRally with options for:
 * new game, save game,load game, stop game and exit
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRallyMenuBar extends MenuBar {

    private AppController appController;

    private Menu controlMenu;

    private MenuItem saveGame;

    private MenuItem newGame;

    private MenuItem loadGame;

    private MenuItem stopGame;

    private MenuItem exitApp;

    /**
     * This method creates a new menu bar with several options
     * @param appController Controller of the app
     */
    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        controlMenu = new Menu("File");
        this.getMenus().add(controlMenu);

        newGame = new MenuItem("New Game");
        newGame.setOnAction( e -> {
            try {
                this.appController.newGame();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        controlMenu.getItems().add(newGame);

        stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction( e -> this.appController.stopGame());
        controlMenu.getItems().add(stopGame);

        saveGame = new MenuItem("Save Game");
        saveGame.setOnAction( e -> this.appController.saveGame());
        controlMenu.getItems().add(saveGame);

        loadGame = new MenuItem("Load Game");
        loadGame.setOnAction( e -> this.appController.loadGame());
        controlMenu.getItems().add(loadGame);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction( e -> this.appController.exit());
        controlMenu.getItems().add(exitApp);

        controlMenu.setOnShowing(e -> update());
        controlMenu.setOnShown(e -> this.updateBounds());
        update();
    }

    /**
     * This method updates the view of the menu depending on the stage of the game
     */
    public void update() {
        if (appController.isGameRunning()) {
            newGame.setVisible(false);
            stopGame.setVisible(true);
            saveGame.setVisible(true);
            loadGame.setVisible(false);
        } else {
            newGame.setVisible(true);
            stopGame.setVisible(false);
            saveGame.setVisible(false);
            loadGame.setVisible(true);
        }
    }

}
