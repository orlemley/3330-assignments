package com.example.haunted.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class WeaponTest extends ItemTest {
    @Override
    protected Item createItem(String name, String description) {
        return new Weapon(name, description, 4);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 4, Integer.MAX_VALUE})
    void attackBonusReturnsConstructorValue(int bonus) {
        Weapon weapon = new Weapon("Sword", "Weapon description.", bonus);

        assertEquals(bonus, weapon.getAttackBonus());
        assertInstanceOf(Equippable.class, weapon);
    }
}
