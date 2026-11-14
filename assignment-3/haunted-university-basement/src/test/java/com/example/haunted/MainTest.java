package com.example.haunted;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.engine.GameEngine;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainTest {
    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private ByteArrayOutputStream output;

    @BeforeEach
    void captureOutput() {
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    @Test
    void mainPrintsExactStartingRoomMovementAndPickupInOrder() {
        Main.main(new String[0]);

        String newline = System.lineSeparator();
        assertEquals("Maintenance Stairwell" + newline
                + "Moved to Abandoned Lecture Hall." + newline
                + "Picked up Coffee Potion." + newline,
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainCreatesGameAndUsesReturnedEngineForAllThreeActions() {
        GameEngine game = mock(GameEngine.class);
        Room room = new Room("test", "Distinct Room", "Test room.");
        when(game.getCurrentRoom()).thenReturn(room);
        when(game.move(Direction.EAST)).thenReturn(new MoveResult(true, "Distinct movement.", false, 0));
        when(game.pickUpItem("Coffee Potion")).thenReturn(new InteractionResult(true, "Distinct pickup."));

        try (MockedStatic<DungeonFactory> factory = mockStatic(DungeonFactory.class)) {
            factory.when(DungeonFactory::createGame).thenReturn(game);

            Main.main(new String[0]);

            factory.verify(DungeonFactory::createGame);
            factory.verifyNoMoreInteractions();
        }
        var calls = inOrder(game);
        calls.verify(game).getCurrentRoom();
        calls.verify(game).move(Direction.EAST);
        calls.verify(game).pickUpItem("Coffee Potion");
        verifyNoMoreInteractions(game);
        String newline = System.lineSeparator();
        assertEquals("Distinct Room" + newline + "Distinct movement." + newline
                + "Distinct pickup." + newline, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void mainCanBeConstructed() {
        assertNotNull(new Main());
    }
}
