package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player createPlayer() {
        return new Player("Student", 50, 7, 2, new Inventory(8));
    }

    @Test
    void constructorInitializesAllFieldsAndDefaults() {
        Inventory inventory = new Inventory(8);
        Player player = new Player("Student", 50, 7, 2, inventory);

        assertEquals("Student", player.getName());
        assertEquals(50, player.getHealth());
        assertEquals(50, player.getMaxHealth());
        assertEquals(7, player.getBaseAttack());
        assertEquals(2, player.getBaseDefense());
        assertSame(inventory, player.getInventory());
        assertNull(player.getEquippedWeapon());
        assertNull(player.getEquippedArmor());
        assertNull(player.getCurrentRoom());
        assertEquals(7, player.getAttackPower());
        assertEquals(2, player.getDefensePower());
        assertTrue(player.isAlive());
    }

    @Test
    void constructorRejectsNullNameAndInventorySeparately() {
        assertThrows(NullPointerException.class, () -> new Player(null, 50, 7, 2, new Inventory(8)));
        assertThrows(NullPointerException.class, () -> new Player("Student", 50, 7, 2, null));
    }

    @ParameterizedTest
    @CsvSource({"-1, false", "0, false", "1, true"})
    void isAliveRequiresPositiveHealth(int health, boolean expected) {
        Player player = new Player("Student", health, 0, 0, new Inventory(0));

        assertEquals(health, player.getHealth());
        assertEquals(health, player.getMaxHealth());
        assertEquals(expected, player.isAlive());
        assertEquals(0, player.getAttackPower());
        assertEquals(0, player.getDefensePower());
    }

    @Test
    void currentRoomCanBeReplacedButCannotBeNull() {
        Player player = createPlayer();
        Room first = new Room("first", "First", "First room.");
        Room second = new Room("second", "Second", "Second room.");
        player.setCurrentRoom(first);
        assertSame(first, player.getCurrentRoom());
        player.setCurrentRoom(second);
        assertSame(second, player.getCurrentRoom());
        assertThrows(NullPointerException.class, () -> player.setCurrentRoom(null));
        assertSame(second, player.getCurrentRoom());
    }

    @ParameterizedTest
    @CsvSource({"-2147483648, 50", "-1, 50", "0, 50", "1, 49", "49, 1",
            "50, 0", "51, 0", "2147483647, 0"})
    void damagePreservesHealthBounds(int damage, int expected) {
        Player player = createPlayer();
        player.takeDamage(damage);

        assertEquals(expected, player.getHealth());
        assertEquals(50, player.getMaxHealth());
        assertEquals(expected > 0, player.isAlive());
        assertTrue(player.getHealth() >= 0 && player.getHealth() <= player.getMaxHealth());
    }

    @ParameterizedTest
    @CsvSource({"-2147483648, 30", "-1, 30", "0, 30", "1, 31", "19, 49", "20, 50", "21, 50"})
    void healingIgnoresNegativeAmountsAndCapsAtMaximum(int healing, int expected) {
        Player player = createPlayer();
        player.takeDamage(20);
        player.heal(healing);

        assertEquals(expected, player.getHealth());
        assertEquals(50, player.getMaxHealth());
    }

    @Test
    void damageAndHealingAccumulateAndHealingCanRevivePlayer() {
        Player player = createPlayer();
        player.takeDamage(10);
        player.takeDamage(15);
        assertEquals(25, player.getHealth());
        player.heal(5);
        player.heal(6);
        assertEquals(36, player.getHealth());
        player.takeDamage(Integer.MAX_VALUE);
        player.takeDamage(Integer.MAX_VALUE);
        assertEquals(0, player.getHealth());
        assertFalse(player.isAlive());
        player.heal(1);
        assertEquals(1, player.getHealth());
        assertTrue(player.isAlive());
    }

    @Test
    void equipmentReplacesCorrectSlotAndAddsBonusesWithoutChangingBaseStats() {
        Player player = createPlayer();
        Weapon firstWeapon = new Weapon("First", "First weapon.", 4);
        Weapon secondWeapon = new Weapon("Second", "Replacement weapon.", 6);
        Armor firstArmor = new Armor("First", "First armor.", 3);
        Armor secondArmor = new Armor("Second", "Replacement armor.", 5);
        player.equipWeapon(firstWeapon);
        assertSame(firstWeapon, player.getEquippedWeapon());
        assertEquals(11, player.getAttackPower());
        assertEquals(2, player.getDefensePower());
        player.equipArmor(firstArmor);
        assertSame(firstArmor, player.getEquippedArmor());
        assertSame(firstWeapon, player.getEquippedWeapon());
        assertEquals(5, player.getDefensePower());
        player.equipWeapon(secondWeapon);
        player.equipArmor(secondArmor);
        assertSame(secondWeapon, player.getEquippedWeapon());
        assertSame(secondArmor, player.getEquippedArmor());
        assertEquals(13, player.getAttackPower());
        assertEquals(7, player.getDefensePower());
        assertEquals(7, player.getBaseAttack());
        assertEquals(2, player.getBaseDefense());
        assertTrue(player.getInventory().getItems().isEmpty());
        assertThrows(NullPointerException.class, () -> player.equipWeapon(null));
        assertThrows(NullPointerException.class, () -> player.equipArmor(null));
        assertSame(secondWeapon, player.getEquippedWeapon());
        assertSame(secondArmor, player.getEquippedArmor());
    }

    @ParameterizedTest
    @CsvSource({"0, 7, 2", "-1, 6, 1"})
    void zeroAndNegativeEquipmentBonusesAreUsedAsSupplied(int bonus, int attack, int defense) {
        Player player = createPlayer();
        player.equipWeapon(new Weapon("Sword", "Weapon.", bonus));
        player.equipArmor(new Armor("Shield", "Armor.", bonus));

        assertEquals(attack, player.getAttackPower());
        assertEquals(defense, player.getDefensePower());
    }
}
