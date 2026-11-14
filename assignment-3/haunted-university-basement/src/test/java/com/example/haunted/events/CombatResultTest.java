package com.example.haunted.events;

import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Potion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CombatResultTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void isSuccessReturnsConstructorValue(boolean success) {
        CombatResult result = new CombatResult(success, "Combat message.", 10, 4, false, List.of());

        assertEquals(success, result.isSuccess());
    }

    @Test
    void getMessageReturnsConstructorValue() {
        CombatResult result = new CombatResult(true, "Attacked Phantom.", 10, 4, false, List.of());

        assertEquals("Attacked Phantom.", result.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void getDamageToMonsterReturnsConstructorValue(int damage) {
        CombatResult result = new CombatResult(true, "Combat message.", damage, 4, false, List.of());

        assertEquals(damage, result.getDamageToMonster());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 4})
    void getDamageToPlayerReturnsConstructorValue(int damage) {
        CombatResult result = new CombatResult(true, "Combat message.", 10, damage, false, List.of());

        assertEquals(damage, result.getDamageToPlayer());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void isMonsterDefeatedReturnsConstructorValue(boolean defeated) {
        CombatResult result = new CombatResult(true, "Combat message.", 10, 4, defeated, List.of());

        assertEquals(defeated, result.isMonsterDefeated());
    }

    @Test
    void getDroppedItemsReturnsExactItemsInOrder() {
        Item potion = new Potion("Coffee", "Dropped potion.", 15);
        Item key = new Key("Vault Key", "Dropped key.");
        CombatResult result = new CombatResult(true, "Defeated Phantom.", 10, 0, true,
                List.of(potion, key));

        assertEquals(List.of(potion, key), result.getDroppedItems());
        assertSame(potion, result.getDroppedItems().get(0));
        assertSame(key, result.getDroppedItems().get(1));
    }

    @Test
    void getDroppedItemsCanReturnEmptyList() {
        CombatResult result = new CombatResult(true, "Attacked Phantom.", 10, 4, false, List.of());

        assertTrue(result.getDroppedItems().isEmpty());
    }

    @Test
    void droppedItemsCannotBeModifiedThroughGetter() {
        Item potion = new Potion("Coffee", "Dropped potion.", 15);
        Item key = new Key("Vault Key", "Dropped key.");
        CombatResult result = new CombatResult(true, "Defeated Phantom.", 10, 0, true,
                List.of(potion, key));
        List<Item> droppedItems = result.getDroppedItems();

        assertThrows(UnsupportedOperationException.class, () -> droppedItems.add(key));
        assertThrows(UnsupportedOperationException.class, () -> droppedItems.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> droppedItems.set(0, key));
        assertThrows(UnsupportedOperationException.class, () -> droppedItems.clear());
        assertEquals(List.of(potion, key), result.getDroppedItems());
    }

    @Test
    void changingOriginalListDoesNotChangeDroppedItems() {
        Item potion = new Potion("Coffee", "Dropped potion.", 15);
        Item key = new Key("Vault Key", "Added later.");
        List<Item> original = new ArrayList<>();
        original.add(potion);
        CombatResult result = new CombatResult(true, "Defeated Phantom.", 10, 0, true, original);

        original.add(key);
        assertEquals(List.of(potion), result.getDroppedItems());
        original.clear();
        assertEquals(List.of(potion), result.getDroppedItems());
        assertSame(potion, result.getDroppedItems().get(0));
    }
}
