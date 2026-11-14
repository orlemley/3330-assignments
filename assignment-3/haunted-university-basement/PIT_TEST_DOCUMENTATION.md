# PIT Mutation Testing Documentation

This file records the mutation-testing results, tests added in response, and the reasoning for surviving mutations. It supplements the exact-prompt history in [AI_USE_DOCUMENTATION_2.md](AI_USE_DOCUMENTATION_2.md). Preserve existing entries when adding later results.

## November 14, 2026 - Scope and verified results

The project uses PIT Maven 1.30.1, pitest-junit5-plugin 1.2.3, and JUnit 6.0.3. Production game code was not changed to improve mutation scores. No methods, classes, or individual survivors were excluded during these audits.

| Run | Scope | Generated | Killed | Survived |
| --- | --- | ---: | ---: | ---: |
| Initial DEFAULTS report, before stronger assertions | Whole project | 250 | 245 | 5 |
| STRONGER after Main, Trap, and rules additions | Whole project | 298 | 297 | 1 |
| ALL after those additions | Rules package | 68 | 67 | 1 |
| ALL before exception-origin assertions | Whole project | 808 | 803 | 5 |
| EXPERIMENTAL_ARGUMENT_PROPAGATION after exception-origin assertions | Room and Monster | 18 | 18 | 0 |

All 354 ordinary test cases passed during the latest targeted verification. The whole-project ALL run reported 379/382 covered lines in mutated classes and zero mutations without coverage. These are different measurements: no uncovered mutations does not mean every source line was covered.

The whole-project ALL run has not been repeated after adding the exception-origin assertions. Its historical score remains 803/808. The targeted run confirms that both former null-check survivors are now killed; a new whole-project score must be measured rather than inferred.

Generated reports, when present locally:

- [Whole-project ALL](target/pit-reports-all/index.html)
- [Whole-project STRONGER](target/pit-reports-stronger/index.html)
- [Rules ALL](target/pit-reports-rules-all/index.html)
- [Targeted null checks](target/pit-reports-null-checks/index.html)

Reports under target are generated artifacts and may disappear after Maven clean.

## Tests added for genuine behavior and diagnostic differences

### Main: missing output and delegation checks

The initial MainTest only asserted that main did not throw. Removing any of its three println calls therefore survived even though the visible output changed.

MainTest now captures System.out and checks the complete exact output, order, and line separators during real factory-backed execution. A separate test supplies a mocked factory result and verifies all four actions: createGame, current-room lookup, movement EAST, and pickup of Coffee Potion. Distinct stubbed messages ensure main prints the returned values rather than hardcoded expected text. System.out is restored after each test.

The three output-removal mutations were killed after these additions.

### Trap: false one-time-trigger flag

The original tests exercised real traps configured as one-time, while the repeatable-trap resolver case used a mocked Trap. Replacing the real Trap.isOneTimeTrigger getter with a constant true therefore survived.

TrapTest now creates real traps across all armed/one-time flag combinations and checks the returned values, including oneTimeTrigger=false. It also checks metadata, null constructor constraints, and idempotent disarming. TrapResolverBehaviorTest verifies actual damage and armed state across two resolutions for both one-time and repeatable traps. The false-flag getter mutation is now killed.

### Monster: explicit null-loot guard

Original constructor expression:

```java
this.loot = new ArrayList<>(Objects.requireNonNull(loot));
```

The argument-propagation mutant removes requireNonNull and passes loot directly to ArrayList. Both versions reject null with NullPointerException, so an assertion checking only the exception type does not distinguish them.

The shared MonsterTest now catches that exception and checks that its first stack frame names java.util.Objects.requireNonNull. StandardMonsterTest and BossMonsterTest inherit the assertion. This verifies the explicit validation origin, rather than relying on a later collection-constructor failure.

PIT's targeted run confirmed the null-loot mutant is KILLED. The test does not assert a source line number or JDK-generated helpful exception text.

### Room: explicit null-direction guard

Original connection expression:

```java
exits.put(Objects.requireNonNull(direction), Objects.requireNonNull(room));
```

Removing the direction guard still causes EnumMap to reject the null key. RoomTest now verifies that the caught exception originates in Objects.requireNonNull, before the map receives the key. The separate null-destination assertion remains in place.

PIT's targeted run confirmed the formerly surviving null-direction mutant is KILLED.

### What these diagnostic tests establish

The null-check mutants are not strictly equivalent when exception stack traces are included in observable behavior. Earlier descriptions calling them simply equivalent were too broad. They preserve the broad game contract of rejecting null, but change the observable rejection origin.

These assertions deliberately require an implementation detail. They may fail after a harmless validation refactor or if stack-trace generation is disabled. They do not inspect PIT state, alter private fields, or change the game's production behavior.

## Remaining survivors identified in the whole-project ALL report

### Inventory.addItem: successful ArrayList.add result replaced with true

Source line 21:

```java
return items.add(item);
```

The surviving mutation is effectively:

```java
items.add(item);
return true;
```

The mutation does not remove the addition. Inventory constructs its private list as an ArrayList and provides no public way to replace that list. ArrayList.add appends the item and returns true when it completes normally. If the addition throws, replacing its eventual return value does not prevent that exception. The separate full-inventory branch still returns false before attempting the addition.

InventoryTest already checks successful addition, exact contents/order, true return values, the capacity boundary, and false returns without changes when full. These assertions test the important behavior and pass in both versions because it is unchanged.

It is reasonable to say Inventory depends on ArrayList behavior, but there is no hidden false-return case of the actual ArrayList.add implementation that another ordinary input test could reveal. Replacing the private collection with an artificial mock that returns false would test a different internal configuration, not one produced through Inventory's public API. That could test a hypothetical delegation contract, but it would not demonstrate a defect with the collection Inventory actually creates.

Assessment: equivalent for the supplied implementation's normal public behavior. Its survival alone does not establish a missing behavioral test.

### CombatEngine.attack: unused initial damage changed from 0 to 1

Source line 38:

```java
int damageToPlayer = 0;
```

PIT's inline-constant mutant changes the initializer to 1. Neither execution path observes this initial value:

| Path after initialization | What happens |
| --- | --- |
| Monster is defeated | The returned CombatResult receives the literal 0; the local variable is not read. |
| Monster survives | calculateMonsterDamage overwrites the local variable before it is used for damage or the result. |

Earlier guard returns occur before this assignment. Therefore no player state, monster state, loot, collaborator call, exception, or returned result depends on the initial zero.

CombatEngineTest already checks that a lethal attack causes no counterattack damage, that a surviving monster causes the exact stubbed damage, and that damage persists across attacks. Those tests correctly pass for this mutant. A test reading compiled instructions could distinguish the constants, but would be asserting representation rather than combat behavior.

Assessment: an overwritten/unused initial value, equivalent for the method's normal runtime behavior. Its survival does not show a missing combat assertion.

### TrapResolver.resolveTrap: formatting array length changed from 2 to 3

The compiler creates an Object array for the two arguments to:

```java
String.format("Trap '%s' triggered for %d damage.", trap.getName(), trap.getDamage())
```

The surviving inline-constant mutation increases this array's length from 2 to 3. Bytecode inspection with javap confirmed that the constant 2 allocates that argument array. The first two entries still contain the same name and damage; the additional entry is unused null. The format has only two conversions, so the third argument is ignored. Health changes, disarming, result status, and message remain the same.

TrapResolver tests check exact result messages and real state changes across all relevant branches, including repeated triggers. These cannot distinguish an unused extra formatting argument.

Assessment: equivalent for normal functional behavior. This statement concerns program results and state, not an assertion of identical allocation size, timing, or resource consumption.

## Why PIT mutates bytecode, including unused instructions

PIT modifies compiled JVM instructions instead of repeatedly editing and compiling Java source. Its documentation describes this as a faster approach that is easier to integrate with builds. Source line information maps the changed instructions back into the HTML report.

PIT generates candidate changes and asks whether tests detect them. It does not first prove that every candidate can affect the program's behavior. The inline-constant operator can change the stored 0 in CombatEngine even though that particular value is never read. Similarly, it can change an array-length constant introduced by compilation rather than written explicitly in the Java source.

This does not mean tests should normally inspect bytecode. It means a bytecode-based mutation generator can produce semantically irrelevant changes. PIT has filters for some equivalent patterns, but the reports above show that not every equivalent case is filtered.

DEFAULTS, STRONGER, and ALL select different sets of mutation operators. ALL means the built-in set for the selected PIT version, not every conceivable edit, every combination of edits, or every input. A different version or external plugin can produce different results.

## Grading interpretation and reproducibility

The surviving Inventory, CombatEngine, and formatting changes above have not been hidden or excluded. They remain visible in the historical report. Their explanations are evidence for instructor review, not a guarantee that a grading script grants credit for equivalence.

If grading requires a literal 100% score against unchanged instructor-supplied production classes, ask how these specific survivors are handled. Structural tests or artificial replacement of private state may change a score without improving verification of the intended game behavior. This document does not assume that the assignment either permits or prohibits those techniques.

To refresh the whole-project result after the null-check assertions:

```powershell
mvn test org.pitest:pitest-maven:mutationCoverage "-Dmutators=ALL" "-DoutputFormats=HTML,XML" "-DreportsDirectory=target/pit-reports-all"
```

## References

- [PIT mutation operators and bytecode approach](https://pitest.org/quickstart/mutators/)
- [PIT Maven configuration](https://pitest.org/quickstart/maven/)
- [ArrayList.add API contract](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html#add(E))
- [Formatter API: extra formatting arguments are ignored](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Formatter.html)
