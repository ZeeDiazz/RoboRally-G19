package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.Client;
import dk.dtu.compute.se.pisd.roborally.online.mvc.client_controller.GameController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class OnlineGame extends Game {
    private Client client;
    private int numberOfPlayersToStart;
    private int interactionsMade = 0;

    /**
     * Constructor used in serializations
     *
     * @author Zigalow
     */
    public OnlineGame(Board board, int gameId, Player current, Phase phase, int step, boolean stepMode, int moveCounter, int numberOfPlayersToStart) {
        super(board, gameId, current, phase, step, stepMode, moveCounter);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    /**
     * Primary constructor for creating an OnlineGame
     *
     * @param board
     */
    public OnlineGame(Board board, int numberOfPlayersToStart) {
        super(board);
        this.numberOfPlayersToStart = numberOfPlayersToStart;
    }

    public int getNumberOfPlayersToStart() {
        return numberOfPlayersToStart;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void setPhase(Phase phase) {
        System.out.println("Set the phase to '" + phase + "'");
        if (phase == Phase.ACTIVATION) {
            for (Player player : players) {
                if (player instanceof LocalPlayer) {
                    CommandCardField[] cardFields = player.getProgram();
                    Command[] commands = new Command[cardFields.length];
                    for (int i = 0; i < cardFields.length; i++) {
                        CommandCard card = cardFields[i].getCard();
                        if (card == null) {
                            commands[i] = null;
                        }
                        else {
                            commands[i] = card.command;
                        }
                        System.out.println("Command: " + commands[i]);
                    }

                    try {
                        client.finishedProgrammingPhase(commands);
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // wait until the other players are also done
            boolean waiting = true;
            while (waiting) {
                try {
                    waiting = !client.canStartActivationPhase();
                } catch (URISyntaxException | IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            client.loadMoves();
        }
        else if (phase == Phase.PLAYER_INTERACTION) {
            Player current = getCurrentPlayer();
            GameController controller = GameController.getInstance();
            if (current instanceof LocalPlayer) {
                super.setPhase(phase);
            }
            else {
                controller.executeCommandOptionAndContinue(Command.MOVE_1/*client.getInteraction(interactionsMade)*/);
            }
            interactionsMade++;
            return;
        }
        super.setPhase(phase);
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();

        jsonObject.addProperty("numberOfPlayersToStart", getNumberOfPlayersToStart());
        return jsonObject;
    }

    public void closeConnection() throws URISyntaxException, IOException, InterruptedException {
        client.deleteActiveGame();
    }

    public void setLocalPlayer(int index) {
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < getPlayerCount(); i++) {
            Player oldPlayer = players.get(i);
            Player newPlayer;
            if (i == index) {
                newPlayer = new LocalPlayer(this, oldPlayer.getRobot().getColor(), oldPlayer.getName());
            }
            else {
                newPlayer = new OnlinePlayer(this, oldPlayer.getRobot().getColor(), oldPlayer.getName());
            }

            newPlayer.setPlayerID(oldPlayer.getPlayerID());
            newPlayer.setPrevProgramming(oldPlayer.getPrevProgramming());
            newPlayer.robot = oldPlayer.robot;

            int currentIndex = 0;
            for (CommandCardField field : oldPlayer.getProgram()) {
                newPlayer.getProgramField(currentIndex++).setCard(field.getCard());
            }
            currentIndex = 0;
            for (CommandCardField field : oldPlayer.getCards()) {
                newPlayer.getCardField(currentIndex++).setCard(field.getCard());
            }

            newPlayers.add(newPlayer);
        }

        this.players = newPlayers;
    }
}
