# AI Use Documentation

This is the append-only record of AI assistance for this assignment. Entries are organized by class or task. Each entry records the student's exact prompt before the assistant's explanation and work results. Future updates will be appended here instead of to README.md.

## November 11, 2026 - DungeonFactoryTest

### Exact student prompt

```text
We need to write tests that give us full test coverage for this already made game for my class. The assignment allows AI. I'll give you specific requirements, and per class, you should maintain a markdown file and don't delete anything in the markdown file as you go. Basically document your response and what you did in the readme so that the teacher can see what you did. Across all of the code, make it very clean and concise and well spaced and human readable, without any too advanced features. I will start with DungeonFactoryTest. I want you to before all the parameterized tests traverse the rooms and get a list of rooms with all their exits, and then use a parameterized test to verify each room has each of the expected exits that are supposed to be created in the dungeon factory. I want a separate test method for the trap in the broken elevator. I want another parameterized test for the locks and keys. I also want you to make a parameterized test somewhere to verify that all of the rooms actually exist as they are defined. Make another parameterized test to test for rooms having items. Make another parameterized test to test for monster-room combinations, and when you do these parameterized tests, hopefully actually for example verify the actual metadata of the monsters rather than just one field of the monster's data. Verify that the player's fields are as defined in a separate test, including the current room. Make another test to verify that the game engine returned contains the correct things and was not made with the wrong objects.
```

### Assistant response and work performed

Implemented the requested tests in DungeonFactoryTest.java. All 45 factory test cases passed, and all 46 tests in the project passed. Production code was unchanged. Coverage percentage was not measured, so 100% coverage was not claimed.

The original detailed class record and README entry are preserved verbatim below as historical records.

### Original detailed class record

# DungeonFactoryTest work log

This file is an append-only record of AI-assisted work for DungeonFactoryTest. Keep existing entries when adding future work.

## November 11, 2026 - Initial tests

### Request and response

The student requested simple, readable JUnit tests for the existing DungeonFactory, with room traversal before parameterized tests, expected exits, room existence, locks and keys, items, full monster metadata, a separate elevator trap test, player fields, and verification of the returned engine. AI assistance was explicitly allowed by the student for this assignment.

Implemented these requests in src/test/java/com/example/haunted/config/DungeonFactoryTest.java. Production game code and the Maven configuration were not changed.

### Implementation

- BeforeEach creates a fresh game and collects reachable Room objects by following every exit. An ordinary list and loop handle cycles without moving the player, unlocking rooms, or triggering traps. Duplicate room IDs must refer to the same object.
- Room metadata: eight parameterized cases verify the complete set of IDs, names, and descriptions. This detects missing and extra reachable rooms.
- Exits: eight parameterized cases verify all 16 directed connections, exact exit counts, directions, and destination object identity. Expectations are written explicitly rather than generated from the factory output.
- Trap: a separate test verifies the broken elevator trap name, electric type, damage of 8, armed state, and one-time setting. Every other room must have no trap.
- Locks and keys: eight parameterized cases check initial locked states and required key names. The two locked rooms also require a Key in the expected room, reject a wrong key, and accept the configured key.
- Items: nine parameterized cases cover all rooms, including empty rooms and both lab storage items. They check exact counts, concrete item types, names, descriptions, and healing/attack/defense bonuses where applicable.
- Monsters: eight parameterized cases cover occupied and empty rooms. They check counts, concrete type, name, current and maximum health, attack, defense, alive state, and complete loot metadata. The boss also has its initial attack and configured enraged attack bonus checked through behavior at half health.
- Player: a separate test checks name, current and maximum health, base and total attack/defense, inventory capacity and contents, alive state, empty equipment, and starting room object identity.
- Engine: a separate test checks quest metadata and starting state, then exercises movement, combat damage, trap resolution, gradebook pickup, and boss defeat. Assertions verify that these actions update the returned player, room objects, inventory, and quest. Direct positioning and reducing boss health keep this wiring check focused and short.
- Independence: another test verifies that separate factory calls return distinct engines, players, quests, inventories, and starting rooms.

### Validation and limits

Ran `mvn -B test`: BUILD SUCCESS. DungeonFactoryTest ran 45 cases with zero failures, errors, or skips. The existing MainTest also passed, giving 46 passing tests overall.

The sandboxed Maven attempt reported access denied; the approved run outside the sandbox completed successfully. Maven emitted the existing source/target 21 system-modules warning.

These tests check the requested factory configuration and public behavior. No coverage instrumentation was run, so no measured line or branch coverage percentage is claimed. This work does not yet provide full coverage for the other game classes. The factory's private utility constructor is not invoked artificially merely to raise coverage.

### Original README entry (moved here)

## AI-assisted test work log

### November 11, 2026 - DungeonFactoryTest

Added the requested factory tests using basic loops, JUnit assertions, and CSV parameterized cases. Tests traverse the room graph before every case and verify all room metadata, exact exits, locks and keys, item metadata, monster metadata and loot, boss attack settings, elevator trap, starting player fields, and returned engine behavior. Separate factory calls are also checked for independent starting objects.

Validation: `mvn -B test` passed all 46 tests, including 45 DungeonFactoryTest cases. No measured coverage percentage is claimed; other game classes remain for later work.

The detailed append-only class record is [DungeonFactoryTest.md](DungeonFactoryTest.md). Future work will append entries without deleting this history.

## November 11, 2026 - Documentation format update

### Exact student prompt

```text
Can you maintain a file called like AI_USE_DOCUMENTATION.md instead of appending to the README, and can you include the exact prompts I put in before your own words too? You can go back and append it this one time to account for these things
```

### Assistant response and work performed

Created AI_USE_DOCUMENTATION.md as the ongoing append-only AI assistance record. Backfilled both exact student prompts, placing each prompt before the assistant's notes for that request. Copied the existing detailed class record and README work log into this file without deleting their history. Removed only the AI work log section previously added to README.md, restoring the original project README content. Kept DungeonFactoryTest.md and appended a pointer to this record so its existing history remains intact.

Future AI work will be documented here by class or task, with the exact student prompt first, followed by the assistant's response, changes, and validation results. No Java code changed during this documentation update; tests were not rerun.
