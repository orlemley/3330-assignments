package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {
    @Test
    void valuesContainExactlyDeclaredConstantsInOrder() {
        Direction[] expected = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        assertArrayEquals(expected, Direction.values());
        Direction[] copy = Direction.values();
        copy[0] = null;
        assertArrayEquals(expected, Direction.values());
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void valueOfReturnsSameConstantForExactName(Direction value) {
        assertSame(value, Direction.valueOf(value.name()));
        assertEquals(value.name(), value.toString());
    }

    @Test
    void valueOfRejectsInvalidNamesAndNull() {
        assertThrows(IllegalArgumentException.class, () -> Direction.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> Direction.valueOf("north"));
        assertThrows(IllegalArgumentException.class, () -> Direction.valueOf(""));
        assertThrows(NullPointerException.class, () -> Direction.valueOf(null));
    }
}
