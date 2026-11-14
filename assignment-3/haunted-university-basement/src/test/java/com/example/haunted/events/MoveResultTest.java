package com.example.haunted.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveResultTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void isSuccessReturnsConstructorValue(boolean success) {
        MoveResult result = new MoveResult(success, "Movement message.", !success, 8);

        assertEquals(success, result.isSuccess());
    }

    @Test
    void getMessageReturnsConstructorValue() {
        MoveResult result = new MoveResult(true, "Moved to Lab Storage.", false, 0);

        assertEquals("Moved to Lab Storage.", result.getMessage());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void isTrapTriggeredReturnsConstructorValue(boolean triggered) {
        MoveResult result = new MoveResult(!triggered, "Movement message.", triggered, 8);

        assertEquals(triggered, result.isTrapTriggered());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 8})
    void getTrapDamageReturnsConstructorValue(int damage) {
        MoveResult result = new MoveResult(true, "Movement message.", true, damage);

        assertEquals(damage, result.getTrapDamage());
    }
}
