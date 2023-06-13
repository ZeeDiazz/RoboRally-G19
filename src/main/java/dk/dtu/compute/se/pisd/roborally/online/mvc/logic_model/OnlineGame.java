package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.online.Client;

import java.io.IOException;
import java.net.URISyntaxException;

public class OnlineGame extends Game {
    private Client client;
    private int numberOfPlayersToStart;

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
        super.setPhase(phase);

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
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = super.serialize().getAsJsonObject();

        jsonObject.addProperty("numberOfPlayersToStart", getNumberOfPlayersToStart());
        return jsonObject;
    }
}
