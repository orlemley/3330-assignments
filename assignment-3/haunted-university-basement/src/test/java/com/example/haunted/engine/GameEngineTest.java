package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameEngineTest {
    private MovementEngine movement;
    private CombatEngine combat;
    private InteractionEngine interaction;
    private GameEngine game;
    private Player player;
    private Quest quest;
    private Room room;

    @BeforeEach
    void setUp() {
        movement = mock(MovementEngine.class);
        combat = mock(CombatEngine.class);
        interaction = mock(InteractionEngine.class);
        player = new Player("Student", 50, 7, 2, new Inventory(8));
        quest = new Quest("Escape", "Recover the gradebook and defeat the phantom.");
        room = new Room("start", "Starting Room", "The original room.");
        player.setCurrentRoom(room);
        game = new GameEngine(player, quest, movement, combat, interaction);
    }

    @AfterEach
    void noUnexpectedEngineCalls() {
        verifyNoMoreInteractions(movement, combat, interaction);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void moveDelegatesAndReturnsExactResult(boolean success) {
        MoveResult expected = new MoveResult(success, "Movement result.", false, 0);
        when(movement.move(player, Direction.EAST)).thenReturn(expected);

        assertSame(expected, game.move(Direction.EAST));

        verify(movement).move(player, Direction.EAST);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void pickupDelegatesWithPlayerQuestAndItemName(boolean success) {
        InteractionResult expected = new InteractionResult(success, "Pickup result.");
        when(interaction.pickUpItem(player, quest, "Lost Gradebook")).thenReturn(expected);

        assertSame(expected, game.pickUpItem("Lost Gradebook"));

        verify(interaction).pickUpItem(player, quest, "Lost Gradebook");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void equipDelegatesAndReturnsExactResult(boolean success) {
        InteractionResult expected = new InteractionResult(success, "Equipment result.");
        when(interaction.equipItem(player, "Sword")).thenReturn(expected);

        assertSame(expected, game.equipItem("Sword"));

        verify(interaction).equipItem(player, "Sword");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void useDelegatesAndReturnsExactResult(boolean success) {
        InteractionResult expected = new InteractionResult(success, "Use result.");
        when(interaction.useItem(player, "Coffee")).thenReturn(expected);

        assertSame(expected, game.useItem("Coffee"));

        verify(interaction).useItem(player, "Coffee");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void unlockDelegatesAndReturnsExactResult(boolean success) {
        InteractionResult expected = new InteractionResult(success, "Unlock result.");
        when(interaction.unlockRoom(player, Direction.NORTH)).thenReturn(expected);

        assertSame(expected, game.unlockRoom(Direction.NORTH));

        verify(interaction).unlockRoom(player, Direction.NORTH);
    }

    @Test
    void attackFindsRealMonsterInPlayersCurrentRoomAndDelegates() {
        Monster previous = new Monster("Phantom", 30, 9, 3, List.of());
        room.addMonster(previous);
        Room current = new Room("current", "Current Room", "Search this room now.");
        Monster other = new Monster("Golem", 28, 7, 4, List.of());
        Monster target = new Monster("Phantom", 20, 8, 2, List.of());
        current.addMonster(other);
        current.addMonster(target);
        player.setCurrentRoom(current);
        CombatResult expected = new CombatResult(true, "Attacked Phantom.", 10, 4, false, List.of());
        when(combat.attack(player, quest, target)).thenReturn(expected);

        assertSame(expected, game.attack("pHaNtOm"));

        verify(combat).attack(player, quest, target);
        assertSame(current, player.getCurrentRoom());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void attackPassesNullWhenRealRoomLookupIsEmpty(boolean hasOtherMonster) {
        if (hasOtherMonster) {
            room.addMonster(new Monster("Golem", 28, 7, 4, List.of()));
        }
        CombatResult expected = new CombatResult(false, "Monster not found.", 0, 0, false, List.of());
        when(combat.attack(player, quest, null)).thenReturn(expected);

        assertSame(expected, game.attack("Phantom"));

        verify(combat).attack(player, quest, null);
    }

    @Test
    void getPlayerReturnsOriginalPlayer() {
        assertSame(player, game.getPlayer());
    }

    @Test
    void getQuestReturnsOriginalQuest() {
        assertSame(quest, game.getQuest());
    }

    @Test
    void getCurrentRoomTracksPlayersRoomChanges() {
        assertSame(room, game.getCurrentRoom());
        Room next = new Room("next", "Next Room", "The new room.");
        player.setCurrentRoom(next);

        assertSame(next, game.getCurrentRoom());
    }

    @ParameterizedTest
    @CsvSource({"0, false", "49, false", "50, true"})
    void gameOverDependsOnWhetherPlayerIsAlive(int damage, boolean expected) {
        player.takeDamage(damage);

        assertEquals(expected, game.isGameOver());
    }

    @ParameterizedTest
    @CsvSource({
            "false, false, false, false",
            "false, false, true, false",
            "true, false, false, false",
            "true, false, true, false",
            "false, true, false, false",
            "false, true, true, false",
            "true, true, false, false",
            "true, true, true, true"
    })
    void gameWonRequiresBothQuestGoalsAndLivingPlayer(boolean gradebookRecovered,
                                                     boolean phantomDefeated,
                                                     boolean alive, boolean expected) {
        if (gradebookRecovered) {
            quest.markGradebookRecovered();
        }
        if (phantomDefeated) {
            quest.markPhantomDefeated();
        }
        if (!alive) {
            player.takeDamage(50);
        }

        assertEquals(expected, game.isGameWon());
    }
}
