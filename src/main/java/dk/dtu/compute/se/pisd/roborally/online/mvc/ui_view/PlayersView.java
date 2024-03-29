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
package dk.dtu.compute.se.pisd.roborally.online.mvc.ui_view;

import dk.dtu.compute.se.pisd.roborally.online.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Game;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.LocalPlayer;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;
import javafx.scene.control.TabPane;

/**
 * ...
 * This class
 * It implements ViewObserver to update the current subject
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayersView extends TabPane implements ViewObserver {


    //ZeeDiazz (Zaid)
    private Game game;

    private PlayerView[] playerViews;

    /**
     * This method creates a new player using the playerView method
     * @param gameController The controller of the game
     */
    public PlayersView(GameController gameController) {
        game = gameController.game;
        gameController.setProgrammingObserver(new ProgrammingObserver() {
            @Override
            public void started() {
                lockNonLocalPlayers();
            }

            @Override
            public void finished() {
                unlockAllPlayers();
            }
        });

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        playerViews = new PlayerView[game.getPlayerCount()];
        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
            playerViews[i] = new PlayerView(gameController, player);

            this.getTabs().add(playerViews[i]);
        }
        lockNonLocalPlayers();
        game.attach(this);
        update(game);
    }

    /**
     * This method updates the view of the current player
     * @param subject Subject is the board
     */
    @Override
    public void updateView(Subject subject) {
        //ZeeDiazz Added game instead of board
        if (subject == game) {
            Player current = game.getCurrentPlayer();
            this.getSelectionModel().select(game.getPlayerNumber(current));
        }
    }

    public void lockNonLocalPlayers() {
        for (int i = 0; i < game.getPlayerCount(); i++) {
            Player player = game.getPlayer(i);
            playerViews[i].setDisable(!(player instanceof LocalPlayer));
            getSelectionModel().select(i); // should work, but doesn't
        }
    }

    public void unlockAllPlayers() {
        for (PlayerView playerView : playerViews) {
            playerView.setDisable(false);
        }
    }
}
