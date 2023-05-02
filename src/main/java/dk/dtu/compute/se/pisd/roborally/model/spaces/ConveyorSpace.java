package dk.dtu.compute.se.pisd.roborally.model.spaces;

import dk.dtu.compute.se.pisd.roborally.model.*;

public abstract class ConveyorSpace extends Space {
    public final Heading direction;
    protected final int amount;

    protected ConveyorSpace(Board board, Position position, Heading direction, int amount, Heading... walls) {
        super(board, position, walls);

        this.direction = direction;
        this.amount = amount;
    }

    @Override
    public Move endedRegisterOn(Player player, int registerIndex) {
        return new Move(this.position, this.direction, this.amount, player);
    }
}
