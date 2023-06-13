package dk.dtu.compute.se.pisd.roborally.online.mvc.logic_model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.online.mvc.saveload.Serializable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Zigalow & ZeeDiazz (Zaid)
 */
public abstract class Player extends Subject implements Serializable {

    final public static int NUMBER_OF_REGISTERS = 5;
    final public static int NUMBER_OF_CARDS = 8;

    private String name;
    private int playerID;   // playerIndex
    public Robot robot;

    //public static Board board;
    public static Game game;

    // In Robot class or this class?
    //private int checkpointReached = 0;
    private int energyCubes;

    private Command prevProgramming;

    private CommandCardField[] programField;
    private CommandCardField[] cards;

    protected Player() {
        this.energyCubes = 5;
        programField = new CommandCardField[NUMBER_OF_REGISTERS];
        for (int i = 0; i < programField.length; i++) {
            programField[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NUMBER_OF_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * Constructor to create a Player object with the given board, color, and name.
     *
     * @param game  The game that the player belongs to.
     * @param color The color of the player.
     * @param name  The name of the player.
     * @author Zaid
     */
    public Player(Game game, String color, @NotNull String name) {
        this.game = game;
        this.name = name;
        this.robot = new Robot(color, this);
        // Player starts with 5 energy cube
        this.energyCubes = 5;
        programField = new CommandCardField[NUMBER_OF_REGISTERS];
        for (int i = 0; i < programField.length; i++) {
            programField[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NUMBER_OF_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }


    /**
     * Gets the name of the player
     *
     * @return players name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the player
     *
     * @param name Sets the players name
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
        }
    }

    /**
     * gets the amount of energy cubes a player has
     *
     * @return the amount of energy cubes
     * @author ZeeDiazz (Zaid)
     */
    public Command getPrevProgramming() {
        return prevProgramming;
    }

    /**
     * Set a programming as previous programming
     *
     * @param programming
     * @author ZeeDiazz (Zaid)
     */
    public void setPrevProgramming(Command programming) {
        prevProgramming = programming;
    }

    /**
     * Takes an amount of energy cube and adds to Players energy cubes
     * If amount is less or equal to 0 it does nothing
     *
     * @param amount of energy cube
     * @author Zeediazz (Zaid)
     */
    public void addEnergyCube(int amount) {
        if (amount > 0) {
            energyCubes += amount;
            notifyChange();
        }
    }

    /**
     * Takes an amount of energy cube and removes X amount of Players energy cubes
     * If amount is less or equal to 0 it does nothing
     *
     * @param amount
     * @author ZeeDiazz (Zaid)
     */
    public void removeEnergyCube(int amount) {
        if (amount > 0) {
            energyCubes -= amount;
            notifyChange();
        }
    }

    /**
     * Gets the program field at the specific index
     *
     * @param index index of the program field
     * @return the command card field at the specific index
     * @author Daniel
     */
    public CommandCardField getProgramField(int index) {
        return programField[index];
    }

    /**
     * Gets the program field at the specific index
     *
     * @param index index of the program field
     * @return the command card field at the specific index
     * @author Daniel
     */
    public CommandCardField getCardField(int index) {
        return cards[index];
    }

    /**
     * @return the commandCardFields in the "command cards" section, in the game
     * @author Zaid
     */
    public CommandCardField[] getCards() {
        return cards;
    }

    /**
     * @return the commandCardFields in the "program" section, in the game
     * @author Zaid
     */
    public CommandCardField[] getProgram() {
        return programField;
    }


    public static void setGame(Game game) {
        Player.game = game;
    }

    public int getPlayerID() {
        return this.playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Robot getRobot() {
        return this.robot;
    }


    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("playerType", this.getClass().getSimpleName());
        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("playerID", this.playerID);
        if (this.prevProgramming != null) {
            jsonObject.addProperty("previousCommand", this.prevProgramming.toString());
        }
        jsonObject.addProperty("energyCubes", this.energyCubes);
        JsonArray jsonArrayProgram = new JsonArray();
        for (CommandCardField cardField : programField) {
            jsonArrayProgram.add(cardField.serialize());
        }
        jsonObject.add("program", jsonArrayProgram);

        JsonArray jsonArrayCards = new JsonArray();
        for (CommandCardField card : cards) {
            jsonArrayCards.add(card.serialize());
        }
        jsonObject.add("cards", jsonArrayCards);

        jsonObject.add("robot", this.robot.serialize());


        return jsonObject;
    }

    @Override
    public Serializable deserialize(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();


        String playerType = jsonObject.getAsJsonPrimitive("playerType").getAsString();

        Player initialPlayer = playerType.equals("OnlinePlayer") ? new OnlinePlayer(null, "red", "") : new LocalPlayer(null, "red", "");

        initialPlayer.name = jsonObject.getAsJsonPrimitive("name").getAsString();
        initialPlayer.playerID = jsonObject.getAsJsonPrimitive("playerID").getAsInt();

        JsonPrimitive jsonCommand = jsonObject.getAsJsonPrimitive("previousCommand");

        String commandAsString = jsonCommand == null ? null : jsonCommand.getAsString();

        Command command = commandAsString == null ? null : Command.valueOf(commandAsString);
        initialPlayer.energyCubes = jsonObject.getAsJsonPrimitive("energyCubes").getAsInt();

        initialPlayer.prevProgramming = command;


        CommandCardField field = new CommandCardField(initialPlayer);
        int index = 0;
        for (JsonElement cardJson : jsonObject.get("cards").getAsJsonArray()) {
            CommandCardField savedField = (CommandCardField) field.deserialize(cardJson);

            initialPlayer.getCardField(index).setCard(savedField.getCard());
            initialPlayer.getCardField(index).setVisible(savedField.isVisible());
            index++;
        }

        field = new CommandCardField(initialPlayer);
        index = 0;
        for (JsonElement cardJson : jsonObject.get("program").getAsJsonArray()) {
            CommandCardField savedField = (CommandCardField) field.deserialize(cardJson);

            initialPlayer.getProgramField(index).setCard(savedField.getCard());
            initialPlayer.getProgramField(index).setVisible(savedField.isVisible());
            index++;
        }


        Robot robot = (Robot) initialPlayer.robot.deserialize(jsonObject.get("robot"));

        initialPlayer.robot = new Robot(robot.getColor(), initialPlayer);

        initialPlayer.robot.checkpointsReached = robot.checkpointsReached;
        initialPlayer.robot.setSpace(robot.getSpace());
        initialPlayer.robot.setRebootPosition(robot.getRebootPosition());
        initialPlayer.robot.setHeadingDirection(robot.getHeadingDirection());

        return initialPlayer;
    }
}
