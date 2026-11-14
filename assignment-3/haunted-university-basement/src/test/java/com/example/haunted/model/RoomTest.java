package com.example.haunted.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {
    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("lab", "Lab Storage", "Storage description.");
    }

    @Test
    void constructorSetsMetadataAndDefaults() {
        assertEquals("lab", room.getId());
        assertEquals("Lab Storage", room.getName());
        assertEquals("Storage description.", room.getDescription());
        assertTrue(room.getExits().isEmpty());
        assertTrue(room.getItems().isEmpty());
        assertTrue(room.getMonsters().isEmpty());
        assertFalse(room.hasLivingMonsters());
        assertFalse(room.isLocked());
        assertNull(room.getRequiredKeyName());
        assertNull(room.getTrap());
    }

    @Test
    void constructorRejectsEachNullFieldButAllowsEmptyStrings() {
        assertThrows(NullPointerException.class, () -> new Room(null, "Name", "Description"));
        assertThrows(NullPointerException.class, () -> new Room("id", null, "Description"));
        assertThrows(NullPointerException.class, () -> new Room("id", "Name", null));
        Room empty = new Room("", "", "");
        assertEquals("", empty.getId());
        assertEquals("", empty.getName());
        assertEquals("", empty.getDescription());
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void connectionsAreDirectionalAndReplaceOnlySelectedExit(Direction direction) {
        Room first = new Room("first", "First", "First room.");
        Room replacement = new Room("second", "Second", "Second room.");
        assertNull(room.getExit(direction));
        room.connect(direction, first);
        assertSame(first, room.getExit(direction));
        assertEquals(Map.of(direction, first), room.getExits());
        assertTrue(first.getExits().isEmpty());
        room.connect(direction, replacement);
        assertSame(replacement, room.getExit(direction));
        assertEquals(1, room.getExits().size());
        for (Direction other : Direction.values()) {
            if (other != direction) {
                assertNull(room.getExit(other));
            }
        }
    }

    @Test
    void exitsRejectNullConnectionsAndExposeUnmodifiableLiveView() {
        Room target = new Room("target", "Target", "Target room.");
        room.connect(Direction.EAST, target);
        Map<Direction, Room> exits = room.getExits();
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> room.connect(null, target));
        // Check that our explicit guard rejects null before EnumMap receives it.
        assertEquals("java.util.Objects", exception.getStackTrace()[0].getClassName());
        assertEquals("requireNonNull", exception.getStackTrace()[0].getMethodName());
        assertThrows(NullPointerException.class, () -> room.connect(Direction.WEST, null));
        assertThrows(UnsupportedOperationException.class, () -> exits.put(Direction.WEST, target));
        assertThrows(UnsupportedOperationException.class, () -> exits.remove(Direction.EAST));
        assertThrows(UnsupportedOperationException.class, exits::clear);
        assertThrows(UnsupportedOperationException.class,
                () -> exits.entrySet().iterator().next().setValue(room));
        room.connect(Direction.NORTH, room);
        assertSame(room, exits.get(Direction.NORTH));
        assertSame(target, exits.get(Direction.EAST));
        assertEquals(2, exits.size());
    }

    @Test
    void itemLookupAndRemovalUseFirstCaseInsensitiveMatch() {
        Item other = new Key("Other", "Not a match.");
        Item first = new Key("Vault Key", "First match.");
        Item second = new Key("VAULT KEY", "Second match.");
        room.addItem(other);
        room.addItem(first);
        room.addItem(second);

        assertSame(first, room.findItem("vault key").orElseThrow());
        assertEquals(List.of(other, first, second), room.getItems());
        assertSame(first, room.removeItemByName("vAuLt KeY"));
        assertEquals(List.of(other, second), room.getItems());
        assertSame(second, room.findItem("Vault Key").orElseThrow());
        assertSame(second, room.removeItemByName("Vault Key"));
        assertEquals(List.of(other), room.getItems());
    }

    @Test
    void missingItemSearchAndRemovalDoNotChangeRoom() {
        assertTrue(room.findItem("Missing").isEmpty());
        assertNull(room.removeItemByName("Missing"));
        Item key = new Key("Key", "Existing item.");
        room.addItem(key);
        assertTrue(room.findItem("Missing").isEmpty());
        assertTrue(room.findItem(null).isEmpty());
        assertNull(room.removeItemByName("Missing"));
        assertNull(room.removeItemByName(null));
        assertEquals(List.of(key), room.getItems());
    }

    @Test
    void itemsRejectNullAndExposeUnmodifiableLiveView() {
        Item first = new Key("First", "First item.");
        Item second = new Key("Second", "Second item.");
        room.addItem(first);
        List<Item> items = room.getItems();
        assertThrows(NullPointerException.class, () -> room.addItem(null));
        assertThrows(UnsupportedOperationException.class, () -> items.add(second));
        assertThrows(UnsupportedOperationException.class, () -> items.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> items.set(0, second));
        assertThrows(UnsupportedOperationException.class, items::clear);
        room.addItem(second);
        assertEquals(List.of(first, second), items);
        room.removeItemByName("First");
        assertEquals(List.of(second), items);
    }

    @Test
    void monsterLookupUsesFirstCaseInsensitiveMatchIncludingDefeatedMonsters() {
        Monster other = monster("Golem", 10);
        Monster first = monster("Phantom", 0);
        Monster second = monster("PHANTOM", 20);
        assertTrue(room.findMonster("Phantom").isEmpty());
        room.addMonster(other);
        room.addMonster(first);
        room.addMonster(second);

        assertSame(first, room.findMonster("pHaNtOm").orElseThrow());
        assertTrue(room.findMonster("Missing").isEmpty());
        assertTrue(room.findMonster(null).isEmpty());
        assertEquals(List.of(other, first, second), room.getMonsters());
    }

    @Test
    void monstersRejectNullAndExposeUnmodifiableLiveView() {
        Monster first = monster("First", 10);
        Monster second = monster("Second", 20);
        room.addMonster(first);
        List<Monster> monsters = room.getMonsters();
        assertThrows(NullPointerException.class, () -> room.addMonster(null));
        assertThrows(UnsupportedOperationException.class, () -> monsters.add(second));
        assertThrows(UnsupportedOperationException.class, () -> monsters.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> monsters.set(0, second));
        assertThrows(UnsupportedOperationException.class, monsters::clear);
        room.addMonster(second);
        assertEquals(List.of(first, second), monsters);
        assertSame(first, monsters.get(0));
        assertSame(second, monsters.get(1));
    }

    @ParameterizedTest
    @CsvSource({"0, 0, false", "0, 1, true", "1, 0, true", "1, 1, true", "-1, 0, false"})
    void livingMonsterCheckHandlesEveryAliveDeadOrdering(int firstHealth, int secondHealth, boolean expected) {
        Monster first = monster("First", firstHealth);
        Monster second = monster("Second", secondHealth);
        room.addMonster(first);
        room.addMonster(second);
        assertEquals(expected, room.hasLivingMonsters());
        first.takeDamage(Integer.MAX_VALUE);
        second.takeDamage(Integer.MAX_VALUE);
        assertFalse(room.hasLivingMonsters());
    }

    @Test
    void setLockedUpdatesBothFieldsAndCanRelockWithDifferentKey() {
        room.setLocked(true, "First Key");
        assertTrue(room.isLocked());
        assertEquals("First Key", room.getRequiredKeyName());
        room.setLocked(false, "Second Key");
        assertFalse(room.isLocked());
        assertEquals("Second Key", room.getRequiredKeyName());
        room.setLocked(true, null);
        assertTrue(room.isLocked());
        assertNull(room.getRequiredKeyName());
        room.setLocked(false, null);
        assertFalse(room.isLocked());
        assertNull(room.getRequiredKeyName());
    }

    @ParameterizedTest
    @CsvSource({"false,, , true", "false, Key, Wrong, true", "true,, Key, false",
            "true,, , false", "true, Key, , false", "true, Key, Wrong, false",
            "true, Key, Key, true", "true, Key, kEy, true"})
    void unlockHandlesLockStateRequiredKeyAndSuppliedKey(boolean locked, String required,
                                                       String supplied, boolean expected) {
        room.setLocked(locked, required);

        assertEquals(expected, room.unlock(supplied));
        assertEquals(locked && !expected, room.isLocked());
        assertEquals(required, room.getRequiredKeyName());
    }

    @Test
    void emptyKeyNamesMatchAndUnlockedRoomStaysUnlockedOnRepeatedCalls() {
        room.setLocked(true, "");
        assertFalse(room.unlock(null));
        assertTrue(room.isLocked());
        assertTrue(room.unlock(""));
        assertFalse(room.isLocked());
        assertTrue(room.unlock("Wrong"));
        assertTrue(room.unlock(null));
        assertFalse(room.isLocked());
    }

    @Test
    void trapCanBeSetReplacedAndCleared() {
        Trap first = new Trap("Wires", TrapType.ELECTRIC, 8, true, true);
        Trap second = new Trap("Steam", TrapType.STEAM, 4, false, false);
        room.setTrap(first);
        assertSame(first, room.getTrap());
        room.setTrap(second);
        assertSame(second, room.getTrap());
        room.setTrap(null);
        assertNull(room.getTrap());
    }

    private Monster monster(String name, int health) {
        return new Monster(name, health, 7, 2, List.of());
    }
}
