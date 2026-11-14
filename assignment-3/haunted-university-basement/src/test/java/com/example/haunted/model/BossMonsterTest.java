package com.example.haunted.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BossMonsterTest extends MonsterTest {
    @Override
    protected Monster createMonster(String name, int health, int attack, int defense, List<Item> loot) {
        return new BossMonster(name, health, attack, defense, loot, 3);
    }

    @ParameterizedTest
    @CsvSource({"40, 0, 10", "40, 19, 10", "40, 20, 13", "40, 21, 13",
            "40, 40, 13", "41, 20, 10", "41, 21, 13"})
    void currentAttackChangesAtHalfHealth(int maxHealth, int damage, int expectedAttack) {
        BossMonster boss = new BossMonster("Phantom", maxHealth, 10, 4, List.of(), 3);
        boss.takeDamage(damage);

        assertEquals(expectedAttack, boss.getCurrentAttack());
        assertEquals(10, boss.getAttack());
    }
}
