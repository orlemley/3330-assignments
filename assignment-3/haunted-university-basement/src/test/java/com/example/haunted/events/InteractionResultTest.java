package com.example.haunted.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InteractionResultTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void isSuccessReturnsConstructorValue(boolean success) {
        InteractionResult result = new InteractionResult(success, "Interaction message.");

        assertEquals(success, result.isSuccess());
    }

    @Test
    void getMessageReturnsConstructorValue() {
        InteractionResult result = new InteractionResult(true, "Picked up Coffee.");

        assertEquals("Picked up Coffee.", result.getMessage());
    }
}
