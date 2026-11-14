package com.example.haunted.model;

import java.util.List;

public class StandardMonsterTest extends MonsterTest {
    @Override
    protected Monster createMonster(String name, int health, int attack, int defense, List<Item> loot) {
        return new Monster(name, health, attack, defense, loot);
    }
}
