package com.example.haunted.rules;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Weapon;
import com.example.haunted.model.Armor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DamageCalculatorTest {
    private DamageCalculator damageCalculator;

    @BeforeEach
    void setUp() {
        damageCalculator = new DamageCalculator();
    }

    @ParameterizedTest
    @CsvSource({
            "20, 2, 18",  // Player attack > Monster defense
            "10, 15, 1",  // Monster defense >= Player attack
    })
    void calculatePlayerDamage_ShouldReturnExpectedDamage(int attack, int defense, int expectedDamage) {
        Monster monster = createMonster(defense);
        Player player = createPlayerWithStats(attack, 10, null, null);

        int damage = damageCalculator.calculatePlayerDamage(player, monster);

        assertEquals(expectedDamage, damage);
    }

    @ParameterizedTest
    @CsvSource({
            "15, 5, 10",  // Monster attack > Player defense
            "5, 10, 1",   // Player defense >= Monster attack
    })
    void calculateMonsterDamage_ShouldReturnExpectedDamage(int monsterAttack, int playerDefense, int expectedDamage) {
        Monster monster = createMonsterWithAttack(monsterAttack);
        Player player = createPlayerWithStats(10, playerDefense, null, null);

        int damage = damageCalculator.calculateMonsterDamage(monster, player);

        assertEquals(expectedDamage, damage);
    }

    @Test
    void calculateMonsterDamage_ShouldIncludeArmorBonus_WhenPlayerHasEquippedArmor() {
        Monster monster = createMonsterWithAttack(20);
        Armor armor = new Armor("Steel Armor", "Reduces damage", 5);
        Player player = createPlayerWithStats(10, 5, null, armor);

        int damage = damageCalculator.calculateMonsterDamage(monster, player);

        assertEquals(10, damage); // 20 - (5 + 5) = 10
    }

    @Test
    void calculatePlayerDamage_ShouldIncludeWeaponBonus_WhenPlayerHasEquippedWeapon() {
        Monster monster = createMonster(10);
        Weapon weapon = new Weapon("Sword of Valor", "Increases attack power", 9);
        Player player = createPlayerWithStats(15, 10, weapon, null);

        int damage = damageCalculator.calculatePlayerDamage(player, monster);

        assertEquals(14, damage); // (15 + 9) - 10 = 14
    }

    @Test
    void calculateMonsterDamage_ShouldAccountForBossMonsterEnragedAttackBonus() {
        BossMonster boss = new BossMonster("Final Boss", 50, 20, 10, Collections.emptyList(), 10); // Proper initialization
        Player player = createPlayerWithStats(10, 10, null, null);

        boss.takeDamage(26); // Boss health drops below 50%
        int damage = damageCalculator.calculateMonsterDamage(boss, player);

        assertEquals(20, damage); // (20 base attack + 10 enraged bonus) - 10 = 20
    }

    // Helper Methods for Test Setup

    private Player createPlayerWithStats(int baseAttack, int baseDefense, Weapon weapon, Armor armor) {
        Inventory inventory = new Inventory(10);
        Player player = new Player("TestPlayer", 50, baseAttack, baseDefense, inventory);
        if (weapon != null) {
            player.equipWeapon(weapon);
        }
        if (armor != null) {
            player.equipArmor(armor);
        }
        return player;
    }

    private Monster createMonster(int defense) {
        // Properly initialize all variables, including loot
        return new Monster("TestMonster", 50, 10, defense, Collections.emptyList());
    }

    private Monster createMonsterWithAttack(int attack) {
        // Properly initialize all variables, including loot
        return new Monster("TestMonster", 50, attack, 10, Collections.emptyList());
    }
}