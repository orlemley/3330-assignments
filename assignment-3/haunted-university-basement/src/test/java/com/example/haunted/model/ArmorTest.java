package com.example.haunted.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ArmorTest extends ItemTest {
    @Override
    protected Item createItem(String name, String description) {
        return new Armor(name, description, 3);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 3, Integer.MAX_VALUE})
    void defenseBonusReturnsConstructorValue(int bonus) {
        Armor armor = new Armor("Shield", "Armor description.", bonus);

        assertEquals(bonus, armor.getDefenseBonus());
        assertInstanceOf(Equippable.class, armor);
    }
}
