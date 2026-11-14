package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class TrapTypeTest {
    @Test
    void valuesContainExactlyDeclaredConstantsInOrder() {
        TrapType[] expected = {TrapType.ELECTRIC, TrapType.STEAM};

        assertArrayEquals(expected, TrapType.values());
        TrapType[] copy = TrapType.values();
        copy[0] = null;
        assertArrayEquals(expected, TrapType.values());
    }

    @ParameterizedTest
    @EnumSource(TrapType.class)
    void valueOfReturnsSameConstantForExactName(TrapType value) {
        assertSame(value, TrapType.valueOf(value.name()));
        assertEquals(value.name(), value.toString());
    }

    @Test
    void valueOfRejectsInvalidNamesAndNull() {
        assertThrows(IllegalArgumentException.class, () -> TrapType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> TrapType.valueOf("electric"));
        assertThrows(IllegalArgumentException.class, () -> TrapType.valueOf(""));
        assertThrows(NullPointerException.class, () -> TrapType.valueOf(null));
    }
}
