package com.example.haunted.rules;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrapResolverTest {
    private TrapResolver trapResolver;

    @BeforeEach
    void setUp() {
        trapResolver = new TrapResolver();
    }

    @Test
    void resolveTrap_ShouldReturnNoTrapTriggered_WhenTrapIsNull() {
        // Arrange
        Player player = mock(Player.class);

        // Act
        InteractionResult result = trapResolver.resolveTrap(player, null);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("No trap was triggered.", result.getMessage());
        verifyNoInteractions(player);
    }

    @Test
    void resolveTrap_ShouldReturnNoTrapTriggered_WhenTrapIsNotArmed() {
        // Arrange
        Player player = mock(Player.class);
        Trap trap = mock(Trap.class);
        when(trap.isArmed()).thenReturn(false);

        // Act
        InteractionResult result = trapResolver.resolveTrap(player, trap);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("No trap was triggered.", result.getMessage());
        verifyNoInteractions(player);
    }

    @Test
    void resolveTrap_ShouldDealDamageAndDisarm_WhenTrapIsArmedAndOneTimeTrigger() {
        // Arrange
        Player player = mock(Player.class);
        Trap trap = mock(Trap.class);

        when(trap.isArmed()).thenReturn(true);
        when(trap.getDamage()).thenReturn(25);
        when(trap.isOneTimeTrigger()).thenReturn(true);
        when(trap.getName()).thenReturn("Electric Shock");

        // Act
        InteractionResult result = trapResolver.resolveTrap(player, trap);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Trap 'Electric Shock' triggered for 25 damage.", result.getMessage());
        verify(player, times(1)).takeDamage(25);
        verify(trap, times(1)).disarm();
    }

    @Test
    void resolveTrap_ShouldDealDamageAndNotDisarm_WhenTrapIsArmedAndNotOneTimeTrigger() {
        // Arrange
        Player player = mock(Player.class);
        Trap trap = mock(Trap.class);

        when(trap.isArmed()).thenReturn(true);
        when(trap.getDamage()).thenReturn(15);
        when(trap.isOneTimeTrigger()).thenReturn(false);
        when(trap.getName()).thenReturn("Steam Jet");

        // Act
        InteractionResult result = trapResolver.resolveTrap(player, trap);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Trap 'Steam Jet' triggered for 15 damage.", result.getMessage());
        verify(player, times(1)).takeDamage(15);
        verify(trap, never()).disarm();
    }
}