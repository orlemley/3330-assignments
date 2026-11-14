package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    @Test
    void newInventoryHasExpectedCapacityAndEmptyContents() {
        Inventory inventory = new Inventory(2);

        assertEquals(2, inventory.getCapacity());
        assertTrue(inventory.getItems().isEmpty());
        assertFalse(inventory.isFull());
    }

    @Test
    void addItemFillsLastSlotAndRejectsFurtherItems() {
        Inventory inventory = new Inventory(2);
        Item first = new Key("First", "First key.");
        Item second = new Key("Second", "Second key.");

        assertTrue(inventory.addItem(first));
        assertFalse(inventory.isFull());
        assertTrue(inventory.addItem(second));
        assertTrue(inventory.isFull());
        assertFalse(inventory.addItem(new Key("Third", "No space.")));
        assertEquals(List.of(first, second), inventory.getItems());
        assertSame(first, inventory.getItems().get(0));
        assertEquals(2, inventory.getCapacity());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void nonpositiveCapacityIsImmediatelyFull(int capacity) {
        Inventory inventory = new Inventory(capacity);

        assertEquals(capacity, inventory.getCapacity());
        assertTrue(inventory.isFull());
        assertFalse(inventory.addItem(new Key("Key", "No space.")));
        assertTrue(inventory.getItems().isEmpty());
    }

    @Test
    void findItemFiltersNonmatchesAndReturnsFirstCaseInsensitiveMatch() {
        Inventory inventory = new Inventory(3);
        Item other = new Key("Other", "Not a match.");
        Item first = new Key("Vault Key", "First match.");
        Item second = new Key("VAULT KEY", "Second match.");
        inventory.addItem(other);
        inventory.addItem(first);
        inventory.addItem(second);

        Optional<Item> found = inventory.findItem("vAuLt KeY");

        assertTrue(found.isPresent());
        assertSame(first, found.orElseThrow());
        assertEquals(List.of(other, first, second), inventory.getItems());
        assertTrue(inventory.contains("vault key"));
        assertFalse(inventory.contains("Missing"));
    }

    @Test
    void missingLookupReturnsEmptyOptionalInEmptyAndPopulatedInventory() {
        Inventory inventory = new Inventory(1);
        assertTrue(inventory.findItem("Missing").isEmpty());
        inventory.addItem(new Key("Other", "Not a match."));

        assertTrue(inventory.findItem("Missing").isEmpty());
        assertTrue(inventory.findItem(null).isEmpty());
        assertFalse(inventory.contains(null));
    }

    @Test
    void removeItemReturnsFirstMatchAndFreesSpace() {
        Inventory inventory = new Inventory(3);
        Item other = new Key("Other", "Must remain.");
        Item first = new Key("Key", "First match.");
        Item second = new Key("KEY", "Second match.");
        inventory.addItem(other);
        inventory.addItem(first);
        inventory.addItem(second);

        assertSame(first, inventory.removeItem("key"));
        assertEquals(List.of(other, second), inventory.getItems());
        assertSame(second, inventory.findItem("Key").orElseThrow());
        assertFalse(inventory.isFull());
        assertTrue(inventory.addItem(first));
        assertTrue(inventory.isFull());
        assertEquals(List.of(other, second, first), inventory.getItems());
    }

    @Test
    void removingMissingItemReturnsNullWithoutChangingContents() {
        Inventory inventory = new Inventory(1);
        assertNull(inventory.removeItem("Missing"));
        Item key = new Key("Key", "Must remain.");
        inventory.addItem(key);

        assertNull(inventory.removeItem("Missing"));
        assertNull(inventory.removeItem(null));
        assertEquals(List.of(key), inventory.getItems());
    }

    @Test
    void itemsViewIsUnmodifiableButReflectsInventoryChanges() {
        Inventory inventory = new Inventory(2);
        Item first = new Key("First", "First key.");
        Item second = new Key("Second", "Second key.");
        inventory.addItem(first);
        List<Item> view = inventory.getItems();

        assertThrows(UnsupportedOperationException.class, () -> view.add(second));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> view.set(0, second));
        assertThrows(UnsupportedOperationException.class, () -> view.clear());
        inventory.addItem(second);
        assertEquals(List.of(first, second), view);
        inventory.removeItem("First");
        assertEquals(List.of(second), view);
    }
}
