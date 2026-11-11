package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CombatEngineTest {
    private DamageCalculator calculator;
    private QuestTracker tracker;
    private CombatEngine engine;
    private Player player;
    private Quest quest;
    private Room startingRoom;
    private Item existingItem;
    private List<Item> loot;

    @BeforeEach
    void setUp() {
        calculator = mock(DamageCalculator.class);
        tracker = mock(QuestTracker.class);
        engine = new CombatEngine(calculator, tracker);
        player = new Player("Student", 50, 7, 2, new Inventory(8));
        quest = new Quest("Escape", "Defeat the phantom.");
        startingRoom = new Room("start", "Starting Room", "The original room.");
        existingItem = new Key("Room Key", "Already in the room.");
        startingRoom.addItem(existingItem);
        player.setCurrentRoom(startingRoom);
        loot = List.of(new Potion("Coffee", "Monster's coffee.", 15),
                new Key("Vault Key", "Monster's key."));
    }

    @Test
    void missingMonsterReturnsExactFailure() {
        assertTrue(player.isAlive());

        CombatResult result = engine.attack(player, quest, null);

        assertResult(result, false, "Monster not found.", 0, 0, false, List.of());
        assertEquals(50, player.getHealth());
        assertEquals(List.of(existingItem), startingRoom.getItems());
        verifyNoInteractions(calculator, tracker);
    }

    @Test
    void defeatedPlayerReturnsExactFailureWithLivingMonster() {
        Monster monster = createMonster(20);
        player.takeDamage(50);
        assertFalse(player.isAlive());
        assertTrue(monster.isAlive());

        CombatResult result = engine.attack(player, quest, monster);

        assertResult(result, false, "Player is defeated.", 0, 0, false, List.of());
        assertEquals(0, player.getHealth());
        assertEquals(20, monster.getHealth());
        assertEquals(List.of(existingItem), startingRoom.getItems());
        verifyNoInteractions(calculator, tracker);
    }

    @Test
    void defeatedMonsterReturnsExactFailureWithLivingPlayer() {
        Monster monster = createMonster(20);
        monster.takeDamage(20);
        assertTrue(player.isAlive());
        assertFalse(monster.isAlive());

        CombatResult result = engine.attack(player, quest, monster);

        assertResult(result, false, "Monster is already defeated.", 0, 0, true, List.of());
        assertEquals(50, player.getHealth());
        assertEquals(0, monster.getHealth());
        assertEquals(List.of(existingItem), startingRoom.getItems());
        verifyNoInteractions(calculator, tracker);
    }

    @Test
    void survivingMonsterCounterattacksAndDamageAccumulatesAcrossTwoAttacks() {
        Monster monster = createMonster(30);
        stubDamage(monster);

        CombatResult first = engine.attack(player, quest, monster);

        assertResult(first, true, "Attacked Phantom.", 10, 4, false, List.of());
        assertEquals(20, monster.getHealth());
        assertEquals(46, player.getHealth());

        CombatResult second = engine.attack(player, quest, monster);

        assertResult(second, true, "Attacked Phantom.", 10, 4, false, List.of());
        assertEquals(10, monster.getHealth());
        assertTrue(monster.isAlive());
        assertEquals(42, player.getHealth());
        assertEquals(List.of(existingItem), startingRoom.getItems());
        verify(calculator, times(2)).calculatePlayerDamage(player, monster);
        verify(calculator, times(2)).calculateMonsterDamage(monster, player);
        verifyNoMoreInteractions(calculator);
        verifyNoInteractions(tracker);
    }

    @Test
    void lethalAttackDropsExactLootWithoutDamagingPlayer() {
        Monster monster = createMonster(10);
        stubDamage(monster);

        CombatResult result = engine.attack(player, quest, monster);

        assertResult(result, true, "Defeated Phantom.", 10, 0, true, loot);
        assertEquals(0, monster.getHealth());
        assertFalse(monster.isAlive());
        assertEquals(50, player.getHealth());
        assertEquals(List.of(existingItem, loot.get(0), loot.get(1)), startingRoom.getItems());
        assertSame(loot.get(0), startingRoom.getItems().get(1));
        assertSame(loot.get(1), startingRoom.getItems().get(2));
        assertTrue(player.getInventory().getItems().isEmpty());
        verify(calculator).calculatePlayerDamage(player, monster);
        verifyNoMoreInteractions(calculator);
        verify(tracker).updateQuestForMonster(quest, monster);
        verifyNoMoreInteractions(tracker);
    }

    @Test
    void secondAttackDefeatsMonsterAndDropsLootInPlayersCurrentRoom() {
        Monster monster = createMonster(20);
        stubDamage(monster);

        CombatResult first = engine.attack(player, quest, monster);

        assertResult(first, true, "Attacked Phantom.", 10, 4, false, List.of());
        assertEquals(10, monster.getHealth());
        assertEquals(46, player.getHealth());
        assertEquals(List.of(existingItem), startingRoom.getItems());
        verifyNoInteractions(tracker);

        Room currentRoom = new Room("current", "Current Room", "Loot belongs here now.");
        player.setCurrentRoom(currentRoom);
        CombatResult second = engine.attack(player, quest, monster);

        assertResult(second, true, "Defeated Phantom.", 10, 0, true, loot);
        assertEquals(0, monster.getHealth());
        // Earlier counterattack damage remains; the killing attack adds none.
        assertEquals(46, player.getHealth());
        assertSame(currentRoom, player.getCurrentRoom());
        assertEquals(loot, currentRoom.getItems());
        assertSame(loot.get(0), currentRoom.getItems().get(0));
        assertSame(loot.get(1), currentRoom.getItems().get(1));
        assertEquals(List.of(existingItem), startingRoom.getItems());
        assertTrue(player.getInventory().getItems().isEmpty());
        verify(calculator, times(2)).calculatePlayerDamage(player, monster);
        verify(calculator).calculateMonsterDamage(monster, player);
        verifyNoMoreInteractions(calculator);
        verify(tracker).updateQuestForMonster(quest, monster);
        verifyNoMoreInteractions(tracker);
    }

    @Test
    void defeatingMonsterWithoutLootStillUpdatesQuest() {
        Monster monster = new Monster("Phantom", 6, 9, 3, List.of());
        stubDamage(monster);

        CombatResult result = engine.attack(player, quest, monster);

        assertResult(result, true, "Defeated Phantom.", 10, 0, true, List.of());
        assertEquals(0, monster.getHealth());
        assertEquals(50, player.getHealth());
        assertEquals(List.of(existingItem), startingRoom.getItems());
        verify(calculator).calculatePlayerDamage(player, monster);
        verifyNoMoreInteractions(calculator);
        verify(tracker).updateQuestForMonster(quest, monster);
        verifyNoMoreInteractions(tracker);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1})
    void monsterHealthBoundaryDeterminesWhetherAttackProceeds(int health) {
        Monster monster = createMonster(health);
        assertTrue(player.isAlive());
        assertEquals(health, monster.getHealth());
        assertEquals(health > 0, monster.isAlive());
        if (health > 0) {
            stubDamage(monster);
        }

        CombatResult result = engine.attack(player, quest, monster);

        assertEquals(50, player.getHealth());
        assertTrue(player.getInventory().getItems().isEmpty());
        assertFalse(monster.isAlive());
        if (health <= 0) {
            assertResult(result, false, "Monster is already defeated.", 0, 0, true, List.of());
            assertEquals(health, monster.getHealth());
            assertEquals(List.of(existingItem), startingRoom.getItems());
            verifyNoInteractions(calculator, tracker);
        } else {
            assertResult(result, true, "Defeated Phantom.", 10, 0, true, loot);
            assertEquals(0, monster.getHealth());
            assertEquals(List.of(existingItem, loot.get(0), loot.get(1)), startingRoom.getItems());
            verify(calculator).calculatePlayerDamage(player, monster);
            verifyNoMoreInteractions(calculator);
            verify(tracker).updateQuestForMonster(quest, monster);
            verifyNoMoreInteractions(tracker);
        }
    }

    private Monster createMonster(int health) {
        return new Monster("Phantom", health, 9, 3, loot);
    }

    private void stubDamage(Monster monster) {
        when(calculator.calculatePlayerDamage(player, monster)).thenReturn(10);
        when(calculator.calculateMonsterDamage(monster, player)).thenReturn(4);
    }

    private void assertResult(CombatResult result, boolean success, String message,
                              int monsterDamage, int playerDamage, boolean defeated,
                              List<Item> expectedLoot) {
        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(monsterDamage, result.getDamageToMonster());
        assertEquals(playerDamage, result.getDamageToPlayer());
        assertEquals(defeated, result.isMonsterDefeated());
        assertEquals(expectedLoot, result.getDroppedItems());
        for (int index = 0; index < expectedLoot.size(); index++) {
            assertSame(expectedLoot.get(index), result.getDroppedItems().get(index));
        }
    }
}
