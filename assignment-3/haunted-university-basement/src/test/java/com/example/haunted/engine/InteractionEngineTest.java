package com.example.haunted.engine;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Armor;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.QuestTracker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InteractionEngineTest {
    private QuestTracker tracker;
    private InteractionEngine engine;
    private Player player;
    private Inventory inventory;
    private Quest quest;
    private Room room;

    @BeforeEach
    void setUp() {
        tracker = mock(QuestTracker.class);
        engine = new InteractionEngine(tracker);
        inventory = new Inventory(3);
        player = new Player("Student", 50, 7, 2, inventory);
        quest = new Quest("Escape", "Recover the gradebook.");
        room = new Room("start", "Starting Room", "The player starts here.");
        player.setCurrentRoom(room);
    }

    @AfterEach
    void noUnexpectedQuestUpdates() {
        // Successful pickups verify their one call; all other cases expect none.
        verifyNoMoreInteractions(tracker);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void missingRoomItemIsRejectedEvenWhenInventoryIsFull(boolean full) {
        if (full) {
            fillInventory(3);
        }
        Item other = new Key("Other Key", "Unrelated room item.");
        room.addItem(other);
        List<Item> before = List.copyOf(inventory.getItems());

        InteractionResult result = engine.pickUpItem(player, quest, "Missing");

        assertResult(result, false, "Item not found in the room.");
        assertEquals(List.of(other), room.getItems());
        assertEquals(before, inventory.getItems());
    }

    @Test
    void fullInventoryReturnsExactItemToCurrentRoom() {
        fillInventory(3);
        List<Item> before = List.copyOf(inventory.getItems());
        Item item = new QuestItem("Lost Gradebook", "The requested item.");
        Item other = new Key("Other Key", "Leave this alone.");
        room.addItem(item);
        room.addItem(other);
        Room otherRoom = new Room("other", "Other Room", "Must stay empty.");
        room.connect(Direction.EAST, otherRoom);

        InteractionResult result = engine.pickUpItem(player, quest, "lost gradebook");

        assertResult(result, false, "Inventory is full.");
        assertEquals(List.of(other, item), room.getItems());
        assertSame(item, room.findItem("Lost Gradebook").orElseThrow());
        assertEquals(before, inventory.getItems());
        assertTrue(otherRoom.getItems().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void successfulPickupTransfersExactItemAndUpdatesTracker(int startingItems) {
        fillInventory(startingItems);
        List<Item> before = List.copyOf(inventory.getItems());
        Item item = new QuestItem("Lost Gradebook", "The requested item.");
        Item other = new Key("Other Key", "Leave this alone.");
        room.addItem(item);
        room.addItem(other);

        InteractionResult result = engine.pickUpItem(player, quest, "lost gradebook");

        assertResult(result, true, "Picked up Lost Gradebook.");
        assertEquals(List.of(other), room.getItems());
        assertTrue(room.findItem("Lost Gradebook").isEmpty());
        assertEquals(startingItems + 1, inventory.getItems().size());
        assertEquals(before, inventory.getItems().subList(0, startingItems));
        assertSame(item, inventory.getItems().get(startingItems));
        verify(tracker).updateQuestForItem(quest, item);
    }

    @Test
    void missingInventoryItemCannotBeUsed() {
        Item other = new Potion("Coffee", "Unrelated potion.", 15);
        inventory.addItem(other);
        player.takeDamage(20);

        assertResult(engine.useItem(player, "Missing"), false, "Item not found in inventory.");

        assertEquals(30, player.getHealth());
        assertEquals(List.of(other), inventory.getItems());
    }

    @Test
    void nonPotionCannotBeUsedAndRemainsInInventory() {
        Item key = new Key("Vault Key", "Not a potion.");
        inventory.addItem(key);
        player.takeDamage(20);

        assertResult(engine.useItem(player, "Vault Key"), false, "That item cannot be used.");

        assertEquals(30, player.getHealth());
        assertEquals(List.of(key), inventory.getItems());
    }

    @ParameterizedTest
    @CsvSource({"20, 45", "5, 50", "0, 50"})
    void potionHealsRealPlayerAndIsConsumed(int initialDamage, int expectedHealth) {
        Potion potion = new Potion("Coffee", "Heals fifteen health.", 15);
        Item other = new Key("Vault Key", "Must remain.");
        inventory.addItem(potion);
        inventory.addItem(other);
        player.takeDamage(initialDamage);

        InteractionResult result = engine.useItem(player, "coffee");

        assertResult(result, true, "Used Coffee.");
        assertEquals(expectedHealth, player.getHealth());
        assertEquals(List.of(other), inventory.getItems());
        assertTrue(inventory.findItem("Coffee").isEmpty());
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    void consumedPotionCannotBeUsedTwice() {
        inventory.addItem(new Potion("Coffee", "Single use.", 15));
        player.takeDamage(40);

        assertResult(engine.useItem(player, "Coffee"), true, "Used Coffee.");
        assertEquals(25, player.getHealth());
        assertTrue(inventory.getItems().isEmpty());
        assertResult(engine.useItem(player, "Coffee"), false, "Item not found in inventory.");
        assertEquals(25, player.getHealth());
    }

    @Test
    void missingItemCannotBeEquippedAndExistingEquipmentRemains() {
        Weapon weapon = new Weapon("Sword", "Existing weapon.", 4);
        Armor armor = new Armor("Shield", "Existing armor.", 3);
        player.equipWeapon(weapon);
        player.equipArmor(armor);

        assertResult(engine.equipItem(player, "Missing"), false, "Item not found in inventory.");

        assertSame(weapon, player.getEquippedWeapon());
        assertSame(armor, player.getEquippedArmor());
        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void weaponIsEquippedAndReplacesOnlyPreviousWeapon() {
        Weapon old = new Weapon("Old Sword", "Previous weapon.", 1);
        Weapon weapon = new Weapon("Sword", "New weapon.", 4);
        Armor armor = new Armor("Shield", "Keep equipped.", 3);
        player.equipWeapon(old);
        player.equipArmor(armor);
        inventory.addItem(weapon);

        assertResult(engine.equipItem(player, "sword"), true, "Equipped weapon Sword.");

        assertSame(weapon, player.getEquippedWeapon());
        assertSame(armor, player.getEquippedArmor());
        assertEquals(11, player.getAttackPower());
        assertEquals(5, player.getDefensePower());
        assertEquals(List.of(weapon), inventory.getItems());
    }

    @Test
    void armorIsEquippedAndReplacesOnlyPreviousArmor() {
        Armor old = new Armor("Old Shield", "Previous armor.", 1);
        Armor armor = new Armor("Shield", "New armor.", 3);
        Weapon weapon = new Weapon("Sword", "Keep equipped.", 4);
        player.equipArmor(old);
        player.equipWeapon(weapon);
        inventory.addItem(armor);

        assertResult(engine.equipItem(player, "shield"), true, "Equipped armor Shield.");

        assertSame(armor, player.getEquippedArmor());
        assertSame(weapon, player.getEquippedWeapon());
        assertEquals(5, player.getDefensePower());
        assertEquals(11, player.getAttackPower());
        assertEquals(List.of(armor), inventory.getItems());
    }

    @Test
    void nonEquipmentReturnsFailureWithoutChangingEquipmentOrInventory() {
        Potion potion = new Potion("Coffee", "Cannot equip.", 15);
        Weapon weapon = new Weapon("Sword", "Keep equipped.", 4);
        Armor armor = new Armor("Shield", "Keep equipped.", 3);
        inventory.addItem(potion);
        player.equipWeapon(weapon);
        player.equipArmor(armor);

        assertResult(engine.equipItem(player, "Coffee"), false, "That item cannot be equipped.");

        assertSame(weapon, player.getEquippedWeapon());
        assertSame(armor, player.getEquippedArmor());
        assertEquals(List.of(potion), inventory.getItems());
        assertEquals(50, player.getHealth());
    }

    @Test
    void missingExitCannotBeUnlockedEvenWithAKey() {
        Key key = new Key("Vault Key", "No target room.");
        inventory.addItem(key);
        Room other = connectTarget(true, "Vault Key");

        assertResult(engine.unlockRoom(player, Direction.WEST), false, "There is no room in that direction.");

        assertTrue(other.isLocked());
        assertSame(room, player.getCurrentRoom());
        assertEquals(List.of(key), inventory.getItems());
    }

    @ParameterizedTest
    @CsvSource({", false", ", true", "Vault Key, false", "Vault Key, true"})
    void alreadyUnlockedRoomNeedsNeitherKeyNorKeyConfiguration(String requiredKey, boolean hasKey) {
        Room target = connectTarget(false, requiredKey);
        if (hasKey) {
            inventory.addItem(new Key("Vault Key", "Optional key."));
        }
        List<Item> before = List.copyOf(inventory.getItems());

        assertResult(engine.unlockRoom(player, Direction.EAST), true, "The room is already unlocked.");

        assertFalse(target.isLocked());
        assertEquals(requiredKey, target.getRequiredKeyName());
        assertSame(room, player.getCurrentRoom());
        assertEquals(before, inventory.getItems());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void lockedRoomWithoutRequiredKeyNameCannotBeUnlocked(boolean hasKey) {
        Room target = connectTarget(true, null);
        if (hasKey) {
            inventory.addItem(new Key("Vault Key", "Cannot fix missing configuration."));
        }
        List<Item> before = List.copyOf(inventory.getItems());

        assertResult(engine.unlockRoom(player, Direction.EAST), false, "The room cannot be unlocked.");

        assertTrue(target.isLocked());
        assertSame(room, player.getCurrentRoom());
        assertEquals(before, inventory.getItems());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void lockedRoomRejectsMissingOrWrongNamedKey(boolean hasWrongKey) {
        Room target = connectTarget(true, "Vault Key");
        if (hasWrongKey) {
            inventory.addItem(new Key("Archive Key", "Wrong key."));
        }
        List<Item> before = List.copyOf(inventory.getItems());

        assertResult(engine.unlockRoom(player, Direction.EAST), false, "You do not have the correct key.");

        assertTrue(target.isLocked());
        assertSame(room, player.getCurrentRoom());
        assertEquals(before, inventory.getItems());
    }

    @Test
    void matchingNameOnNonKeyDoesNotUnlockRoom() {
        Room target = connectTarget(true, "Vault Key");
        Item impostor = new Potion("Vault Key", "Correct name but wrong type.", 15);
        inventory.addItem(impostor);

        assertResult(engine.unlockRoom(player, Direction.EAST), false, "You do not have the correct key.");

        assertTrue(target.isLocked());
        assertEquals(List.of(impostor), inventory.getItems());
        assertSame(room, player.getCurrentRoom());
    }

    @Test
    void correctKeyUnlocksOnlyTargetAndIsNotConsumed() {
        Room target = connectTarget(true, "Vault Key");
        Room other = new Room("other", "Other Room", "Must remain locked.");
        other.setLocked(true, "Vault Key");
        room.connect(Direction.NORTH, other);
        Key key = new Key("vAuLt kEy", "Case-insensitive match.");
        inventory.addItem(key);

        assertResult(engine.unlockRoom(player, Direction.EAST), true, "Unlocked Vault.");

        assertFalse(target.isLocked());
        assertEquals("Vault Key", target.getRequiredKeyName());
        assertTrue(other.isLocked());
        assertSame(room, player.getCurrentRoom());
        assertEquals(List.of(key), inventory.getItems());
        assertSame(key, inventory.findItem("Vault Key").orElseThrow());
        assertResult(engine.unlockRoom(player, Direction.EAST), true, "The room is already unlocked.");
        assertEquals(List.of(key), inventory.getItems());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void firstMatchingItemDeterminesUnlockWhenNamesAreDuplicated(boolean keyFirst) {
        Room target = connectTarget(true, "Vault Key");
        Key key = new Key("Vault Key", "The real key.");
        Potion impostor = new Potion("Vault Key", "Same name as the key.", 15);
        inventory.addItem(keyFirst ? key : impostor);
        inventory.addItem(keyFirst ? impostor : key);
        List<Item> before = List.copyOf(inventory.getItems());

        InteractionResult result = engine.unlockRoom(player, Direction.EAST);

        assertResult(result, keyFirst, keyFirst ? "Unlocked Vault." : "You do not have the correct key.");
        assertEquals(!keyFirst, target.isLocked());
        assertEquals(before, inventory.getItems());
        assertSame(room, player.getCurrentRoom());
    }

    @Test
    void ordinaryItemPickupAlsoNotifiesTracker() {
        Item item = new Key("Archive Key", "Not a quest item.");
        room.addItem(item);

        assertResult(engine.pickUpItem(player, quest, "Archive Key"), true, "Picked up Archive Key.");

        assertTrue(room.getItems().isEmpty());
        assertEquals(List.of(item), inventory.getItems());
        verify(tracker).updateQuestForItem(quest, item);
    }

    private Room connectTarget(boolean locked, String requiredKey) {
        Room target = new Room("vault", "Vault", "The target room.");
        target.setLocked(locked, requiredKey);
        room.connect(Direction.EAST, target);
        return target;
    }

    private void fillInventory(int count) {
        for (int index = 0; index < count; index++) {
            assertTrue(inventory.addItem(new Key("Key " + index, "Existing item.")));
        }
    }

    private void assertResult(InteractionResult result, boolean success, String message) {
        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
    }
}
