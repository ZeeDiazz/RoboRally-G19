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
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Command;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Phase;
import dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model.Player;


import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class PlayerView extends Tab implements ViewObserver {

    private Player player;

    private VBox top;

    private Label programLabel;
    private GridPane programPane;
    private Label cardsLabel;
    private GridPane cardsPane;

    private CardFieldView[] programCardViews;
    private CardFieldView[] cardViews;

    private VBox buttonPanel;

    private Button finishButton;
    private Button executeButton;
    private Button stepButton;

    private VBox playerInteractionPanel;

    private GameController gameController;

    /**
     * This method creates a new player(view) that can view its cards and the buttons:
     * finish, execute and step
     *
     * @param gameController Controller of the game
     * @param player         The one player to be created
     */
    public PlayerView(@NotNull GameController gameController, @NotNull Player player) {
        super(player.getName());
        this.setStyle("-fx-text-base-color: " + player.robot.getColor() + ";");

        top = new VBox();
        this.setContent(top);

        this.gameController = gameController;
        this.player = player;

        programLabel = new Label("Program");

        programPane = new GridPane();
        programPane.setVgap(2.0);
        programPane.setHgap(2.0);
        programCardViews = new CardFieldView[Player.NUMBER_OF_REGISTERS];
        for (int i = 0; i < Player.NUMBER_OF_REGISTERS; i++) {
            CommandCardField cardField = player.getProgramField(i);
            if (cardField != null) {
                programCardViews[i] = new CardFieldView(gameController, cardField);
                programPane.add(programCardViews[i], i, 0);
            }
        }

        // XXX  the following buttons should actually not be on the tabs of the individual
        //      players, but on the PlayersView (view for all players). This should be
        //      refactored.

        finishButton = new Button("Finish Programming");
        finishButton.setOnAction(e -> gameController.finishProgrammingPhase());

        executeButton = new Button("Execute Program");
        executeButton.setOnAction(e -> gameController.executePrograms());

        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction(e -> gameController.executeStep());

        buttonPanel = new VBox(finishButton, executeButton, stepButton);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.setSpacing(3.0);
        // programPane.add(buttonPanel, Player.NO_REGISTERS, 0); done in update now

        playerInteractionPanel = new VBox();
        playerInteractionPanel.setAlignment(Pos.CENTER_LEFT);
        playerInteractionPanel.setSpacing(3.0);

        cardsLabel = new Label("Command Cards");
        cardsPane = new GridPane();
        cardsPane.setVgap(2.0);
        cardsPane.setHgap(2.0);
        cardViews = new CardFieldView[Player.NUMBER_OF_CARDS];
        for (int i = 0; i < Player.NUMBER_OF_CARDS; i++) {
            CommandCardField cardField = player.getCardField(i);
            if (cardField != null) {
                cardViews[i] = new CardFieldView(gameController, cardField);
                cardsPane.add(cardViews[i], i, 0);
            }
        }

        top.getChildren().add(programLabel);
        top.getChildren().add(programPane);
        top.getChildren().add(cardsLabel);
        top.getChildren().add(cardsPane);

        if (player.game != null) {
            player.game.attach(this);
            update(player.game);
        }   
    }

    /**
     * This method updates the player view if certain states of the game changes
     *
     * @param subject The subject of the game which was updated (board, player or phase)
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == player.game) {
            for (int i = 0; i < Player.NUMBER_OF_REGISTERS; i++) {
                CardFieldView cardFieldView = programCardViews[i];
                if (cardFieldView != null) {
                    if (player.game.getPhase() == Phase.PROGRAMMING) {
                        cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                        //cardFieldView.imageView.setImage(null);
                    } else {
                        if (i < player.game.getStep()) {
                            cardFieldView.setBackground(CardFieldView.BG_DONE);
                        } else if (i == player.game.getStep()) {
                            if (player.game.getCurrentPlayer() == player) {
                                cardFieldView.setBackground(CardFieldView.BG_ACTIVE);
                            } else if (player.game.getPlayerNumber(player.game.getCurrentPlayer()) > player.game.getPlayerNumber(player)) {
                                cardFieldView.setBackground(CardFieldView.BG_DONE);
                            } else {
                                cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                            }
                        } else {
                            cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                            cardFieldView.imageView.setImage(null);
                        }
                    }
                }
            }

            if (player.game.getPhase() != Phase.PLAYER_INTERACTION) {
                if (!programPane.getChildren().contains(buttonPanel)) {
                    programPane.getChildren().remove(playerInteractionPanel);
                    programPane.add(buttonPanel, Player.NUMBER_OF_REGISTERS, 0);
                }
                switch (player.game.getPhase()) {
                    case INITIALISATION:
                        finishButton.setDisable(true);
                        // XXX just to make sure that there is a way for the player to get
                        //     from the initialization phase to the programming phase somehow!
                        executeButton.setDisable(false);
                        stepButton.setDisable(true);
                        break;

                    case PROGRAMMING:
                        finishButton.setDisable(false);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                        break;

                    case ACTIVATION:
                        finishButton.setDisable(true);
                        executeButton.setDisable(false);
                        stepButton.setDisable(false);
                        break;

                    default:
                        finishButton.setDisable(true);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                }


            } else {
                if (!programPane.getChildren().contains(playerInteractionPanel)) {
                    programPane.getChildren().remove(buttonPanel);
                    programPane.add(playerInteractionPanel, Player.NUMBER_OF_REGISTERS, 0);
                }
                playerInteractionPanel.getChildren().clear();


                // Zigalow {
                if (player.game.getCurrentPlayer() == player && this.player.game.getPhase() == Phase.PLAYER_INTERACTION) {
                    // Makes buttons for all the different options relating to the interactive card

                    for (Command option : this.gameController.game.currentInteractiveCard.getOptions()) {
                        Button optionButton = new Button(option.displayName);
                        optionButton.setOnAction(e -> gameController.executeCommandOptionAndContinue(option)); // The option which is chosen gets handled by this
                        optionButton.setDisable(false);
                        playerInteractionPanel.getChildren().add(optionButton);
                    }

                    // Zigalow }


                    /*Button optionButton = new Button("Option1");
                    optionButton.setOnAction( e -> gameController.notImplemented());
                    optionButton.setDisable(false);
                    playerInteractionPanel.getChildren().add(optionButton);

                    optionButton = new Button("Option 2");
                    optionButton.setOnAction( e -> gameController.notImplemented());
                    optionButton.setDisable(false);
                    playerInteractionPanel.getChildren().add(optionButton);*/
                }
            }
        }
    }

}
