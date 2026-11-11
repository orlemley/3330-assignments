package com.example.haunted.engine;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.rules.TrapResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovementEngineTest {
    private TrapResolver resolver;
    private MovementEngine engine;
    private Player player;
    private Room startingRoom;
    private Room targetRoom;
    private Trap startingTrap;

    @BeforeEach
    void setUp() {
        resolver = mock(TrapResolver.class);
        engine = new MovementEngine(resolver);
        player = new Player("Student", 50, 7, 2, new Inventory(8));
        startingRoom = new Room("start", "Starting Room", "The original room.");
        targetRoom = new Room("target", "Lab Storage", "The destination room.");
        startingRoom.connect(Direction.EAST, targetRoom);
        player.setCurrentRoom(startingRoom);

        // A different trap in the starting room catches use of the wrong room.
        startingTrap = new Trap("Starting Trap", TrapType.STEAM, 20, true, true);
        startingRoom.setTrap(startingTrap);
    }

    @Test
    void missingExitReturnsExactFailureAndDoesNotMovePlayer() {
        assertNull(startingRoom.getExit(Direction.WEST));

        MoveResult result = engine.move(player, Direction.WEST);

        assertResult(result, false, "There is no room in that direction.", false, 0);
        assertSame(startingRoom, player.getCurrentRoom());
        assertEquals(50, player.getHealth());
        verifyNoInteractions(resolver);
    }

    @Test
    void lockedDestinationReturnsExactFailureWithoutResolvingItsArmedTrap() {
        Trap trap = new Trap("Destination Trap", TrapType.ELECTRIC, 8, true, true);
        targetRoom.setTrap(trap);
        targetRoom.setLocked(true, "Lab Key");
        assertSame(targetRoom, startingRoom.getExit(Direction.EAST));

        MoveResult result = engine.move(player, Direction.EAST);

        assertResult(result, false, "The room is locked.", false, 0);
        assertSame(startingRoom, player.getCurrentRoom());
        assertTrue(targetRoom.isLocked());
        assertTrue(trap.isArmed());
        assertEquals(50, player.getHealth());
        verifyNoInteractions(resolver);
    }

    @Test
    void armedDestinationTrapIsPassedToResolverAndItsResultIsIncluded() {
        Trap trap = new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true);
        targetRoom.setTrap(trap);
        InteractionResult trapResult = new InteractionResult(true,
                "Trap 'Loose Wires Trap' triggered for 8 damage.");
        when(resolver.resolveTrap(player, trap)).thenReturn(trapResult);

        MoveResult result = engine.move(player, Direction.EAST);

        assertResult(result, true,
                "Moved to Lab Storage. Trap 'Loose Wires Trap' triggered for 8 damage.", true, 8);
        assertSame(targetRoom, player.getCurrentRoom());
        assertSame(trap, player.getCurrentRoom().getTrap());
        assertSame(startingTrap, startingRoom.getTrap());
        // The resolver is mocked; its damage/disarming rules are tested separately.
        assertEquals(50, player.getHealth());
        verify(resolver).resolveTrap(player, trap);
        verifyNoMoreInteractions(resolver);
    }

    @Test
    void nullDestinationTrapAllowsMovementWithoutCallingResolver() {
        assertNull(targetRoom.getTrap());

        MoveResult result = engine.move(player, Direction.EAST);

        assertResult(result, true, "Moved to Lab Storage.", false, 0);
        assertSame(targetRoom, player.getCurrentRoom());
        assertNull(player.getCurrentRoom().getTrap());
        assertEquals(50, player.getHealth());
        assertTrue(startingTrap.isArmed());
        verifyNoInteractions(resolver);
    }

    @Test
    void disarmedDestinationTrapAllowsMovementWithoutCallingResolver() {
        Trap trap = new Trap("Destination Trap", TrapType.ELECTRIC, 8, false, true);
        targetRoom.setTrap(trap);

        MoveResult result = engine.move(player, Direction.EAST);

        assertResult(result, true, "Moved to Lab Storage.", false, 0);
        assertSame(targetRoom, player.getCurrentRoom());
        assertSame(trap, player.getCurrentRoom().getTrap());
        assertFalse(trap.isArmed());
        assertEquals(50, player.getHealth());
        verifyNoInteractions(resolver);
    }

    private void assertResult(MoveResult result, boolean success, String message,
                              boolean trapTriggered, int trapDamage) {
        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(trapTriggered, result.isTrapTriggered());
        assertEquals(trapDamage, result.getTrapDamage());
    }
}
