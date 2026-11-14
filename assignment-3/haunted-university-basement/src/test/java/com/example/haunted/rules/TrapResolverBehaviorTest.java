package com.example.haunted.rules;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TrapResolverBehaviorTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void realTrapDealsDamageAndOnlyOneTimeTrapStopsAfterFirstTrigger(boolean oneTime) {
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        Trap trap = new Trap("Steam Jet", TrapType.STEAM, 15, true, oneTime);
        TrapResolver resolver = new TrapResolver();

        InteractionResult first = resolver.resolveTrap(player, trap);

        assertTrue(first.isSuccess());
        assertEquals("Trap 'Steam Jet' triggered for 15 damage.", first.getMessage());
        assertEquals(35, player.getHealth());
        assertEquals(!oneTime, trap.isArmed());
        InteractionResult second = resolver.resolveTrap(player, trap);
        assertEquals(!oneTime, second.isSuccess());
        assertEquals(oneTime ? "No trap was triggered." : "Trap 'Steam Jet' triggered for 15 damage.",
                second.getMessage());
        assertEquals(oneTime ? 35 : 20, player.getHealth());
        assertEquals(!oneTime, trap.isArmed());
        assertEquals(oneTime, trap.isOneTimeTrigger());
    }

    @Test
    void nullAndDisarmedRealTrapsDoNotDamagePlayer() {
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        TrapResolver resolver = new TrapResolver();
        Trap trap = new Trap("Steam", TrapType.STEAM, 15, false, false);
        InteractionResult missing = resolver.resolveTrap(player, null);
        InteractionResult disarmed = resolver.resolveTrap(player, trap);

        assertFalse(missing.isSuccess());
        assertEquals("No trap was triggered.", missing.getMessage());
        assertFalse(disarmed.isSuccess());
        assertEquals("No trap was triggered.", disarmed.getMessage());
        assertEquals(50, player.getHealth());
        assertFalse(trap.isArmed());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 50, 51})
    void trapCanTriggerWithZeroOrLethalDamage(int damage) {
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        Trap trap = new Trap("Wires", TrapType.ELECTRIC, damage, true, true);

        InteractionResult result = new TrapResolver().resolveTrap(player, trap);

        assertTrue(result.isSuccess());
        assertEquals("Trap 'Wires' triggered for " + damage + " damage.", result.getMessage());
        assertEquals(Math.max(0, 50 - damage), player.getHealth());
        assertFalse(trap.isArmed());
    }
}
