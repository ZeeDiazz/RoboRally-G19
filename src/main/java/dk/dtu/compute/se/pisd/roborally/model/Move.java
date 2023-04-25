package dk.dtu.compute.se.pisd.roborally.model;

public final class Move {
    public final Position Start;
    public final Heading Direction;
    public final int Amount;
    public final Player Moving;

    public Move(Position start, Heading direction, int amount, Player moving) {
        this.Start = start;
        this.Direction = direction;
        this.Amount = amount;
        this.Moving = moving;
    }

    public Position getEndingPosition() {
        return Position.move(Start, Direction, Amount);
    }

    public static Move fromPlayer(Player player, int amount) {
        return new Move(player.getSpace().Position, player.getHeading(), amount, player);
    }
}
