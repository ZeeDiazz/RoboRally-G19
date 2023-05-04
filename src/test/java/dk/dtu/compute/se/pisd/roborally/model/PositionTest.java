package dk.dtu.compute.se.pisd.roborally.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void sameCoordinatesDifferentObjectsGiveSameHashCode() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(0, 0);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void sameCoordinatesDifferentOrderGiveDifferentHashCodes() {
        Position p1 = new Position(0, 1);
        Position p2 = new Position(1, 0);

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void differentCoordinatesGiveDifferentHashCodes() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(1, 1);

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void moveSouthWorks() {
        Position p = new Position(0, 0);

        Position expected = new Position(0, 1);
        Position actual = Position.move(p, Heading.SOUTH);
        assertEquals(expected, actual);
    }

    @Test
    void moveNorthWorks() {
        Position p = new Position(0, 0);

        Position expected = new Position(0, -1);
        Position actual = Position.move(p, Heading.NORTH);
        assertEquals(expected, actual);
    }

    @Test
    void moveWestWorks() {
        Position p = new Position(0, 0);

        Position expected = new Position(-1, 0);
        Position actual = Position.move(p, Heading.WEST);
        assertEquals(expected, actual);
    }

    @Test
    void moveEastWorks() {
        Position p = new Position(0, 0);

        Position expected = new Position(1, 0);
        Position actual = Position.move(p, Heading.EAST);
        assertEquals(expected, actual);
    }

    @Test
    void moveWithoutAmountEquivalentToMoveWithAmountEqualOne() {
        Position p = new Position(0, 0);
        Position noAmount = Position.move(p, Heading.SOUTH);
        Position withAmount = Position.move(p, Heading.SOUTH, 1);

        assertEquals(withAmount, noAmount);
    }

    @Test
    void consecutiveMoveWithoutAmountEquivalentToMoveWithAmount() {
        Position p = new Position(0, 0);
        Position withAmount = Position.move(p, Heading.SOUTH, 4);
        Position noAmount = Position.move(p, Heading.SOUTH);
        noAmount = Position.move(noAmount, Heading.SOUTH);
        noAmount = Position.move(noAmount, Heading.SOUTH);
        noAmount = Position.move(noAmount, Heading.SOUTH);

        assertEquals(withAmount, noAmount);
    }

    @Test
    void addingPointsOrderDoesNotMatter() {
        Position p1 = new Position(1, 1);
        Position p2 = new Position(3, 5);

        Position p1p2 = Position.add(p1, p2);
        Position p2p1 = Position.add(p2, p1);

        assertEquals(p1p2, p2p1);
    }

    @Test
    void addTwoPositivePoints() {
        Position p1 = new Position(1, 1);
        Position p2 = new Position(3, 5);

        Position expected = new Position(4, 6);
        Position actual = Position.add(p1, p2);

        assertEquals(expected, actual);
    }

    @Test
    void addOnePositivePointWithOneNegativePoint() {
        Position p1 = new Position(-1, -1);
        Position p2 = new Position(3, 5);

        Position expected = new Position(2, 4);
        Position actual = Position.add(p1, p2);

        assertEquals(expected, actual);
    }

    @Test
    void addTwoNegativePoints() {
        Position p1 = new Position(-1, -1);
        Position p2 = new Position(-3, -5);

        Position expected = new Position(-4, -6);
        Position actual = Position.add(p1, p2);

        assertEquals(expected, actual);
    }

    @Test
    void deserializingSerializedPositionGivesOriginal() {
        Position position = new Position(0, 0);

        Position deserialized = (Position)position.deserialize(position.serialize());

        assertEquals(position, deserialized);
    }
}