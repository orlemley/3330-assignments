package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class MonsterTest {
    protected abstract Monster createMonster(String name, int health, int attack, int defense, List<Item> loot);

    @Test
    void constructorSetsAllFields() {
        Item key = new Key("Key", "Loot.");
        Monster monster = createMonster("Phantom", 30, 9, 3, List.of(key));

        assertEquals("Phantom", monster.getName());
        assertEquals(30, monster.getHealth());
        assertEquals(30, monster.getMaxHealth());
        assertEquals(9, monster.getAttack());
        assertEquals(3, monster.getDefense());
        assertTrue(monster.isAlive());
        assertEquals(List.of(key), monster.getLoot());
        assertSame(key, monster.getLoot().get(0));
    }

    @Test
    void zeroValuesAndEmptyLootArePreserved() {
        Monster monster = createMonster("Empty", 0, 0, 0, List.of());

        assertEquals(0, monster.getHealth());
        assertEquals(0, monster.getMaxHealth());
        assertEquals(0, monster.getAttack());
        assertEquals(0, monster.getDefense());
        assertFalse(monster.isAlive());
        assertTrue(monster.getLoot().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"-1, false", "0, false", "1, true"})
    void aliveDependsOnPositiveHealth(int health, boolean alive) {
        Monster monster = createMonster("Phantom", health, 9, 3, List.of());

        assertEquals(health, monster.getHealth());
        assertEquals(alive, monster.isAlive());
    }

    @ParameterizedTest
    @CsvSource({"-2147483648, 20", "-1, 20", "0, 20", "1, 19",
            "19, 1", "20, 0", "21, 0", "2147483647, 0"})
    void damageIgnoresNegativeValuesAndClampsHealthAtZero(int damage, int expectedHealth) {
        Monster monster = createMonster("Phantom", 20, 9, 3, List.of());

        monster.takeDamage(damage);

        assertEquals(expectedHealth, monster.getHealth());
        assertEquals(20, monster.getMaxHealth());
        assertEquals(expectedHealth > 0, monster.isAlive());
        assertTrue(monster.getHealth() >= 0 && monster.getHealth() <= monster.getMaxHealth());
    }

    @Test
    void repeatedExtremeDamageCannotOverflowOrReviveDefeatedMonster() {
        Monster monster = createMonster("Phantom", Integer.MAX_VALUE, 9, 3, List.of());
        monster.takeDamage(1);
        assertEquals(Integer.MAX_VALUE - 1, monster.getHealth());
        monster.takeDamage(Integer.MIN_VALUE);
        assertEquals(Integer.MAX_VALUE - 1, monster.getHealth());
        monster.takeDamage(Integer.MAX_VALUE);
        assertEquals(0, monster.getHealth());
        monster.takeDamage(Integer.MAX_VALUE);
        monster.takeDamage(Integer.MIN_VALUE);
        assertEquals(0, monster.getHealth());
        assertEquals(Integer.MAX_VALUE, monster.getMaxHealth());
        assertFalse(monster.isAlive());
    }

    @Test
    void damageAccumulatesWithoutChangingMetadataOrLoot() {
        Item key = new Key("Key", "Loot.");
        Monster monster = createMonster("Phantom", 20, 9, 3, List.of(key));
        monster.takeDamage(6);
        monster.takeDamage(7);

        assertEquals(7, monster.getHealth());
        assertEquals(20, monster.getMaxHealth());
        assertEquals("Phantom", monster.getName());
        assertEquals(9, monster.getAttack());
        assertEquals(3, monster.getDefense());
        assertEquals(List.of(key), monster.getLoot());
    }

    @Test
    void lootIsUnmodifiableAndCopiedFromOriginalList() {
        Item key = new Key("Key", "Loot.");
        Item potion = new Potion("Coffee", "Loot.", 15);
        List<Item> original = new ArrayList<>(List.of(key, potion));
        Monster monster = createMonster("Phantom", 20, 9, 3, original);
        original.clear();
        original.add(potion);
        List<Item> loot = monster.getLoot();

        assertEquals(List.of(key, potion), loot);
        assertSame(key, loot.get(0));
        assertSame(potion, loot.get(1));
        assertThrows(UnsupportedOperationException.class, () -> loot.add(key));
        assertThrows(UnsupportedOperationException.class, () -> loot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> loot.set(0, potion));
        assertThrows(UnsupportedOperationException.class, () -> loot.clear());
        assertEquals(List.of(key, potion), monster.getLoot());
    }

    @Test
    void nullNameAndLootAreRejectedSeparately() {
        assertThrows(NullPointerException.class, () -> createMonster(null, 20, 9, 3, List.of()));
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> createMonster("Phantom", 20, 9, 3, null));

        // Check that our explicit guard rejects null before ArrayList receives it.
        assertEquals("java.util.Objects", exception.getStackTrace()[0].getClassName());
        assertEquals("requireNonNull", exception.getStackTrace()[0].getMethodName());
    }
}
