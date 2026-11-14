package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {
    @ParameterizedTest
    @CsvSource({"true, true, ELECTRIC, 8", "true, false, STEAM, 15",
            "false, true, STEAM, 0", "false, false, ELECTRIC, -1"})
    void constructorPreservesFieldsAndDisarmOnlyChangesArmedState(boolean armed, boolean oneTime,
                                                                TrapType type, int damage) {
        Trap trap = new Trap("Test Trap", type, damage, armed, oneTime);

        assertEquals("Test Trap", trap.getName());
        assertEquals(type, trap.getType());
        assertEquals(damage, trap.getDamage());
        assertEquals(armed, trap.isArmed());
        assertEquals(oneTime, trap.isOneTimeTrigger());
        trap.disarm();
        trap.disarm();
        assertFalse(trap.isArmed());
        assertEquals(oneTime, trap.isOneTimeTrigger());
        assertEquals(damage, trap.getDamage());
        assertEquals(type, trap.getType());
        assertEquals("Test Trap", trap.getName());
    }

    @Test
    void constructorRejectsNullNameAndTypeSeparately() {
        assertThrows(NullPointerException.class, () -> new Trap(null, TrapType.STEAM, 8, true, false));
        assertThrows(NullPointerException.class, () -> new Trap("Steam", null, 8, true, false));
    }
}
