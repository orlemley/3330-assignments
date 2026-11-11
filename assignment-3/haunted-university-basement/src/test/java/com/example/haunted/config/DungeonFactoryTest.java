package com.example.haunted.config;

import com.example.haunted.engine.GameEngine;
import com.example.haunted.events.CombatResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DungeonFactoryTest {
    private GameEngine game;
    private Map<String, Room> rooms;

    @BeforeEach
    void collectRoomsAndExits() {
        game = DungeonFactory.createGame();
        rooms = new LinkedHashMap<>();
        List<Room> pendingRooms = new ArrayList<>();
        pendingRooms.add(game.getCurrentRoom());

        // Follow exits directly without triggering traps or changing locked rooms.
        for (int index = 0; index < pendingRooms.size(); index++) {
            Room room = pendingRooms.get(index);
            if (rooms.containsKey(room.getId())) {
                assertSame(rooms.get(room.getId()), room, "Room IDs must be unique");
                continue;
            }
            rooms.put(room.getId(), room);
            pendingRooms.addAll(room.getExits().values());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "stairwell, Maintenance Stairwell, The flickering stairwell leading into the basement.",
            "lectureHall, Abandoned Lecture Hall, Dusty desks and a broken projector.",
            "labStorage, Lab Storage, Shelves of outdated hardware and chemicals.",
            "brokenElevator, Broken Elevator, The elevator doors hang over a dark shaft.",
            "serverCloset, Server Closet, A warm humming room full of blinking servers.",
            "examArchive, Exam Archive, Cabinets of old exams and paperwork.",
            "deanVault, Dean Vault, A hidden chamber guarded by university secrets.",
            "finalChamber, Final Chamber, A spectral exam hall where the boss waits."
    })
    void roomsHaveExpectedMetadata(String id, String name, String description) {
        assertEquals(Set.of("stairwell", "lectureHall", "labStorage", "brokenElevator",
                "serverCloset", "examArchive", "deanVault", "finalChamber"), rooms.keySet());
        Room room = rooms.get(id);
        assertNotNull(room);
        assertEquals(id, room.getId());
        assertEquals(name, room.getName());
        assertEquals(description, room.getDescription());
    }

    @ParameterizedTest
    @CsvSource({
            "stairwell, EAST:lectureHall",
            "lectureHall, WEST:stairwell EAST:labStorage SOUTH:brokenElevator NORTH:examArchive",
            "labStorage, WEST:lectureHall NORTH:serverCloset",
            "brokenElevator, NORTH:lectureHall",
            "serverCloset, SOUTH:labStorage EAST:deanVault",
            "examArchive, SOUTH:lectureHall EAST:deanVault",
            "deanVault, WEST:examArchive SOUTH:serverCloset NORTH:finalChamber",
            "finalChamber, SOUTH:deanVault"
    })
    void roomsHaveExactlyExpectedExits(String id, String expectedExits) {
        Room room = rooms.get(id);
        String[] exits = expectedExits.split(" ");
        assertEquals(exits.length, room.getExits().size());
        for (String exit : exits) {
            String[] parts = exit.split(":");
            assertSame(rooms.get(parts[1]), room.getExit(Direction.valueOf(parts[0])), exit);
        }
    }

    @Test
    void brokenElevatorHasExpectedTrap() {
        Trap trap = rooms.get("brokenElevator").getTrap();
        assertNotNull(trap);
        assertEquals("Loose Wires Trap", trap.getName());
        assertEquals(TrapType.ELECTRIC, trap.getType());
        assertEquals(8, trap.getDamage());
        assertTrue(trap.isArmed());
        assertTrue(trap.isOneTimeTrigger());
        for (Room room : rooms.values()) {
            if (!room.getId().equals("brokenElevator")) {
                assertNull(room.getTrap(), room.getId());
            }
        }
    }

    @ParameterizedTest
    @CsvSource({
            "stairwell, false,,",
            "lectureHall, false,,",
            "labStorage, false,,",
            "brokenElevator, false,,",
            "serverCloset, false,,",
            "examArchive, true, Archive Key, labStorage",
            "deanVault, false,,",
            "finalChamber, true, Vault Key, deanVault"
    })
    void roomsHaveExpectedLocksAndKeys(String id, boolean locked, String keyName, String keyRoom) {
        Room room = rooms.get(id);
        assertEquals(locked, room.isLocked());
        assertEquals(keyName, room.getRequiredKeyName());
        if (locked) {
            Item key = rooms.get(keyRoom).findItem(keyName).orElseThrow();
            assertInstanceOf(Key.class, key);
            assertFalse(room.unlock("Wrong Key"));
            assertTrue(room.isLocked());
            assertTrue(room.unlock(key.getName()));
            assertFalse(room.isLocked());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "stairwell, 0,,,, 0",
            "lectureHall, 1, Potion, Coffee Potion, A suspiciously warm cup of coffee., 15",
            "labStorage, 2, Key, Archive Key, Opens the Exam Archive., 0",
            "labStorage, 2, Armor, Calculator Shield, A battered calculator used as a shield., 3",
            "brokenElevator, 0,,,, 0",
            "serverCloset, 1, Weapon, Stapler of Justice, A heavy-duty stapler with righteous force., 4",
            "examArchive, 1, QuestItem, Lost Gradebook, The legendary missing gradebook., 0",
            "deanVault, 1, Key, Vault Key, Opens the Final Chamber., 0",
            "finalChamber, 0,,,, 0"
    })
    void roomsHaveExpectedItems(String id, int count, String type, String name,
                               String description, int bonus) {
        Room room = rooms.get(id);
        assertEquals(count, room.getItems().size());
        if (count > 0) {
            assertItem(room.findItem(name).orElseThrow(), type, name, description, bonus);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "stairwell,, 0, 0, 0,,,, 0",
            "lectureHall, Sleep-Deprived TA, 18, 6, 1, Potion, Coffee Potion, A backup coffee drop., 15",
            "labStorage,, 0, 0, 0,,,, 0",
            "brokenElevator,, 0, 0, 0,,,, 0",
            "serverCloset, Spreadsheet Golem, 28, 7, 4, Weapon, Dry Erase Sword, An improvised marker tray blade., 6",
            "examArchive, Plagiarism Ghost, 22, 8, 2,,,, 0",
            "deanVault, Registrar Wraith, 30, 9, 3, Armor, Graduation Gown Armor, Ceremonial robes with uncanny resilience., 5",
            "finalChamber, Final Exam Phantom, 40, 10, 4,,,, 0"
    })
    void roomsHaveExpectedMonsters(String id, String name, int health, int attack, int defense,
                                  String lootType, String lootName, String lootDescription, int lootBonus) {
        List<Monster> monsters = rooms.get(id).getMonsters();
        assertEquals(name == null ? 0 : 1, monsters.size());
        if (name == null) {
            return;
        }
        Monster monster = monsters.get(0);
        assertEquals(name, monster.getName());
        assertEquals(health, monster.getHealth());
        assertEquals(health, monster.getMaxHealth());
        assertEquals(attack, monster.getAttack());
        assertEquals(defense, monster.getDefense());
        assertTrue(monster.isAlive());
        assertEquals(lootName == null ? 0 : 1, monster.getLoot().size());
        if (lootName != null) {
            assertItem(monster.getLoot().get(0), lootType, lootName, lootDescription, lootBonus);
        }
        if (id.equals("finalChamber")) {
            BossMonster boss = assertInstanceOf(BossMonster.class, monster);
            assertEquals(10, boss.getCurrentAttack());
            boss.takeDamage(20);
            assertEquals(13, boss.getCurrentAttack());
        } else {
            assertEquals(Monster.class, monster.getClass());
        }
    }

    private void assertItem(Item item, String type, String name, String description, int bonus) {
        assertEquals(type, item.getClass().getSimpleName());
        assertEquals(name, item.getName());
        assertEquals(description, item.getDescription());
        switch (type) {
            case "Potion":
                assertEquals(bonus, ((Potion) item).getHealingAmount());
                break;
            case "Weapon":
                assertEquals(bonus, ((Weapon) item).getAttackBonus());
                break;
            case "Armor":
                assertEquals(bonus, ((Armor) item).getDefenseBonus());
                break;
        }
    }

    @Test
    void playerHasExpectedStartingFields() {
        Player player = game.getPlayer();
        assertEquals("Student Explorer", player.getName());
        assertEquals(50, player.getHealth());
        assertEquals(50, player.getMaxHealth());
        assertEquals(7, player.getBaseAttack());
        assertEquals(7, player.getAttackPower());
        assertEquals(2, player.getBaseDefense());
        assertEquals(2, player.getDefensePower());
        assertEquals(8, player.getInventory().getCapacity());
        assertTrue(player.getInventory().getItems().isEmpty());
        assertFalse(player.getInventory().isFull());
        assertTrue(player.isAlive());
        assertNull(player.getEquippedWeapon());
        assertNull(player.getEquippedArmor());
        assertSame(rooms.get("stairwell"), player.getCurrentRoom());
        assertSame(player.getCurrentRoom(), game.getCurrentRoom());
    }

    @Test
    void returnedEngineUsesExpectedPlayerQuestAndCollaborators() {
        Player player = game.getPlayer();
        Quest quest = game.getQuest();
        assertEquals("Escape the Basement", quest.getName());
        assertEquals("Recover the Lost Gradebook and defeat the Final Exam Phantom.", quest.getDescription());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertFalse(game.isGameOver());
        assertFalse(game.isGameWon());

        assertTrue(game.move(Direction.EAST).isSuccess());
        assertSame(rooms.get("lectureHall"), player.getCurrentRoom());
        CombatResult combat = game.attack("Sleep-Deprived TA");
        assertTrue(combat.isSuccess());
        assertEquals(6, combat.getDamageToMonster());
        assertEquals(4, combat.getDamageToPlayer());
        assertEquals(12, rooms.get("lectureHall").getMonsters().get(0).getHealth());
        assertEquals(46, player.getHealth());

        MoveResult move = game.move(Direction.SOUTH);
        assertTrue(move.isSuccess());
        assertTrue(move.isTrapTriggered());
        assertEquals(8, move.getTrapDamage());
        assertEquals(38, player.getHealth());
        assertFalse(rooms.get("brokenElevator").getTrap().isArmed());

        // Position directly to check wiring without replaying the entire dungeon.
        player.setCurrentRoom(rooms.get("examArchive"));
        Item gradebook = rooms.get("examArchive").getItems().get(0);
        assertTrue(game.pickUpItem("Lost Gradebook").isSuccess());
        assertSame(gradebook, player.getInventory().getItems().get(0));
        assertTrue(quest.isGradebookRecovered());
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());

        player.setCurrentRoom(rooms.get("finalChamber"));
        Monster boss = rooms.get("finalChamber").getMonsters().get(0);
        boss.takeDamage(39);
        assertTrue(game.attack("Final Exam Phantom").isMonsterDefeated());
        assertTrue(quest.isPhantomDefeated());
        assertTrue(game.isGameWon());
    }

    @Test
    void separateGamesDoNotShareMutableObjects() {
        GameEngine other = DungeonFactory.createGame();
        assertNotSame(game, other);
        assertNotSame(game.getPlayer(), other.getPlayer());
        assertNotSame(game.getQuest(), other.getQuest());
        assertNotSame(game.getPlayer().getInventory(), other.getPlayer().getInventory());
        assertNotSame(game.getCurrentRoom(), other.getCurrentRoom());
    }
}
