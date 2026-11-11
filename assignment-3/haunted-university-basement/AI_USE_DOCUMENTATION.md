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

## November 11, 2026 - CombatEngineTest

### Exact student prompt

```text
You can still include summaries of work performed and of my prompts too if you think that they would be useful, but just make sure to prominently display the exact prompts I send because the assignment requires them. Next, let's do the CombatEngineTest. First, make tests for the 3 invariants in attack(), and verify the exact CombatResults that they return. You should isolate all three so that another one could not cause the early return. I don't want to test damageCalculator directly here, so there is an opportunity for mocking here. Let's make a mock of the calculator so that it always returns like a fixed amount of damage. You can choose the amount based on what kinds of values you want the other tests to use. Use mockito for this. Use a real monster though and verify that it actually takes the damage. We should have separate tests for if the monster died in the attack or if the monster did not die, since this fact causes a huge split in the following behavior. Verify that the monster's exact loot actually gets dropped into both of the areas that it should when it gets defeated (Including the correct current room). Make sure that there is a test where two attacks happen but the monster eventually gets defeated and still test the defeat behavior. I also do not want to test QuestTracker directly here. Mock it and verify that it gets called with the generally correct arguments. For every test that goes into the defeated branch, make sure that the correct combat result is returned, unless it gets to be repetitive and we already have that behavior fully verified. Use the same stub as before for the player damage, and make sure that the player gets damaged and for the one/ones that do two attacks, make sure the player is still damaged even during the second attack phase (i,e, that it did not reset somewhere). Use a real player for this. For the attacks where the monster does not get defeated, verify the exact fields of the CombatResult unless it gets repetitive. Also make sure that you test somewhere that the player does NOT get damaged if the attack results in the monster being defeated.
```

### Request summary

Test CombatEngine.attack in isolation from DamageCalculator and QuestTracker using Mockito, while retaining real player, monster, room, and loot objects. Cover the three guard clauses, surviving and defeated monsters, repeated attacks, exact results, loot placement, and persistent player damage.

### Assistant response and work performed

Added src/test/java/com/example/haunted/engine/CombatEngineTest.java and a test-scoped mockito-core 5.23.0 dependency in pom.xml (a version already present in the local Maven cache). Production code was unchanged.

Seven tests cover:

- Missing monster with a living player, a defeated player with a non-null living monster, and an already defeated monster with a living player. Each checks every CombatResult field, unchanged health/room items, and no interactions with either mock.
- Two nonlethal attacks with monster health 30 -> 20 -> 10 and player health 50 -> 46 -> 42. Each result is checked exactly; no loot drops or quest updates occur.
- Immediate defeat at exactly 10 health: both exact loot objects appear in the result and current room, existing room items remain, inventory stays empty, the tracker receives the actual quest and monster once, and the player takes no damage.
- Defeat on the second attack: player health stays at 46 after the lethal attack, preserving the first counterattack's damage. Changing the player's current room between attacks verifies that loot lands in the current room rather than the original room. Both result objects are checked exactly.
- Defeat with empty loot and overkill damage: the result reports the stubbed damage, monster health reaches zero, no loot is added, the tracker is still called, and player health stays unchanged.

The calculator is mocked to return 10 damage to the monster and 4 damage to the player for every valid attack. Tests verify exact calculator arguments and call counts; lethal attacks must not request counterattack damage. QuestTracker is mocked, and tests verify calls with the real quest and defeated monster without exercising quest rules. A small assertion helper checks all six CombatResult fields and loot object identity for every result.

### Validation

The initial sandboxed Maven run reported access denied. A Maven run outside the sandbox was requested to compile and execute the tests; its final result will be appended below.

Validation result: `mvn -B test` completed with BUILD SUCCESS. All 7 CombatEngineTest tests passed; all 53 project tests passed with zero failures, errors, or skips. Maven emitted the existing Java source/target warning and Mockito's dynamic-agent attachment warnings; these did not prevent the tests from running. No measured coverage percentage is claimed.

## November 11, 2026 - CombatEngineTest monster health boundaries

### Exact student prompt

```text
If you didn't already, can you include parameterized boundary value tests somewhere just for monster health being -1,0, and 1?
```

### Assistant response and work performed

Added a parameterized test using @ValueSource(ints = {-1, 0, 1}) to CombatEngineTest. Each case uses a real monster and living player. At -1 and 0 health, the test checks the exact already-defeated CombatResult, unchanged monster health, no loot drops, and no interactions with the calculator or tracker. At 1 health, the test checks that combat proceeds, the existing fixed damage stub defeats the monster, the exact success result and loot are returned, loot reaches the current room, and the quest tracker receives the correct objects. All cases verify that player health remains 50 and inventory remains empty.

Validation: `mvn -B test` completed with BUILD SUCCESS. All 10 CombatEngineTest cases passed, and all 56 project tests passed with no failures, errors, or skips. Production code and README were unchanged.

## November 11, 2026 - InteractionEngineTest

### Exact student prompt

```text
Next, let's do InteractionEngineTest. Generally use real objects but mock quest tracker and verify its method calls when they happen with correct arguments. For pickUpItem, Make a test for the item == null case and both of the fields of the returned object. Use real objects for the next branch where if not inventory.addItem, and validate that the item really does get readded to the room, and that the returned object is correct in both its fields. Make sure that you verify the calls to the mocked questTracker. In cases where neither other branch are hit, validate the returned result and make sure that the item is actually gone from the room. For useItem(), validate the case where the optional is empty, then validate the non potion branch in another test. Verify that the potion actually gets used, and use a real object to verify that whatever that call's effects are supposed to be actually happen. Make a test for the fact that the item was removed from the player's inventory. Validate the returned result in the cases of neither of the invariant branches being hit. For equipItem(), validate the empty optional branch, the weapon branch, and the armor branch, including the actual equip calls working and the returned object's fields. Then validate the default returned object's values for if none of the branches get entered. for unlockRoom(), validate every branch, using real objects and real calls on their methods. Make sure to cover any relevant combinations of things that could happen, if there is any conceivable way that the behaviors could differ based on the interactions of different factors. Validate the method call and the return at the end too.
```

### Request summary

Test every InteractionEngine branch with real game objects and a mocked QuestTracker. Check both InteractionResult fields, actual state changes, and correct tracker arguments. Include relevant combinations affecting room unlocking.

### Assistant response and work performed

Added src/test/java/com/example/haunted/engine/InteractionEngineTest.java. Only QuestTracker is mocked; players, inventories, rooms, quests, keys, potions, weapons, armor, and quest items are real. Production code and README were unchanged.

- Pickup: missing items with empty and full inventory; restoration of the exact item to the current room when full; successful pickup with an empty inventory and the last free slot; ordinary-item and quest-item pickups. Tests check exact success/message fields, remaining room items, inventory contents and identity, and tracker calls with the actual quest and item. The full-inventory test also confirms an adjacent room stays empty.
- Use: missing item, non-potion rejection, actual healing, maximum-health capping, consumption even at full health, retention of unrelated inventory items, and failure when trying to use a consumed potion again. Both result fields are checked on every call.
- Equip: missing item, successful weapon and armor replacement, and non-equipment rejection. Tests verify exact equipped objects, derived attack/defense values, retention of the other equipment slot and inventory items, and both result fields.
- Unlock: missing exit, already unlocked rooms with and without required-key configuration and with and without a key, locked rooms without a required key name, absent or wrong-named keys, matching names on non-key items, and successful case-insensitive key matching. Tests check actual lock changes, exact results, retained keys, unchanged current room, and unrelated locked rooms. Repeated unlocking must report already unlocked.
- Duplicate-name combinations: parameterized cases put the matching real Key before or after a non-key with the same name. They document existing first-match behavior: a non-key first match blocks unlocking even if a real key follows it. No production behavior was changed.
- An AfterEach verification rejects unexpected tracker calls. Successful pickups explicitly verify their single updateQuestForItem call; all other interactions must leave the tracker unused.

A small shared assertion helper checks both InteractionResult fields. Parameterized cases use straightforward CSV and value sources to keep combinations readable.

### Validation

Ran `mvn -B test` after the final additions: BUILD SUCCESS. All 29 InteractionEngineTest cases passed; all 85 project tests passed with zero failures, errors, or skips. The existing compiler and Mockito agent warnings remained. No coverage instrumentation was run, so no measured coverage percentage is claimed.
