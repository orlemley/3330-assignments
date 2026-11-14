package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class QuestTest {
    @Test
    void constructorSetsMetadataAndUnstartedDefaults() {
        Quest quest = new Quest("Escape", "Recover and defeat.");

        assertEquals("Escape", quest.getName());
        assertEquals("Recover and defeat.", quest.getDescription());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertFalse(quest.isComplete());
        quest.updateStatus();
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
    }

    @Test
    void nullMetadataIsRejectedAndEmptyMetadataIsAllowed() {
        assertThrows(NullPointerException.class, () -> new Quest(null, "Description."));
        assertThrows(NullPointerException.class, () -> new Quest("Name", null));
        Quest quest = new Quest("", "");
        assertEquals("", quest.getName());
        assertEquals("", quest.getDescription());
    }

    @ParameterizedTest
    @CsvSource({"false, false, NOT_STARTED", "true, false, IN_PROGRESS",
            "false, true, IN_PROGRESS", "true, true, COMPLETED"})
    void everyGoalCombinationProducesExpectedStatus(boolean gradebook, boolean phantom, QuestStatus status) {
        Quest quest = new Quest("Escape", "Recover and defeat.");
        if (gradebook) {
            quest.markGradebookRecovered();
        }
        if (phantom) {
            quest.markPhantomDefeated();
        }

        assertEquals(gradebook, quest.isGradebookRecovered());
        assertEquals(phantom, quest.isPhantomDefeated());
        assertEquals(status, quest.getStatus());
        assertEquals(gradebook && phantom, quest.isComplete());
        quest.updateStatus();
        quest.updateStatus();
        assertEquals(status, quest.getStatus());
        assertEquals(gradebook && phantom, quest.isComplete());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void goalsCanBeCompletedInEitherOrderAndRepeatedWithoutRegression(boolean gradebookFirst) {
        Quest quest = new Quest("Escape", "Recover and defeat.");
        if (gradebookFirst) {
            quest.markGradebookRecovered();
            quest.markGradebookRecovered();
        } else {
            quest.markPhantomDefeated();
            quest.markPhantomDefeated();
        }
        assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus());
        assertEquals(gradebookFirst, quest.isGradebookRecovered());
        assertEquals(!gradebookFirst, quest.isPhantomDefeated());
        assertFalse(quest.isComplete());

        if (gradebookFirst) {
            quest.markPhantomDefeated();
        } else {
            quest.markGradebookRecovered();
        }
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
        quest.markGradebookRecovered();
        quest.markPhantomDefeated();
        quest.updateStatus();
        assertTrue(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
        assertEquals("Escape", quest.getName());
        assertEquals("Recover and defeat.", quest.getDescription());
    }

    @Test
    void updateStatusDoesNotChangeCompletedQuest() {
        Quest quest = new Quest("Escape", "Recover and defeat.");
        quest.markGradebookRecovered();
        quest.markPhantomDefeated();
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());

        // Call updateStatus and ensure state is unchanged
        quest.updateStatus();
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
        assertTrue(quest.isComplete());
    }

    @Test
    void nullQuestNameOrDescriptionThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Quest(null, "Description"));
        assertThrows(NullPointerException.class, () -> new Quest("Name", null));
    }

    @Test
    void emptyQuestNameAndDescription() {
        Quest quest = new Quest("", "");
        assertEquals("", quest.getName());
        assertEquals("", quest.getDescription());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertFalse(quest.isComplete());
    }

    @Test
    void repeatedMarkingOfGoalsDoesNotAffectState() {
        Quest quest = new Quest("Escape", "Recover and defeat.");

        // Mark goals multiple times
        quest.markGradebookRecovered();
        quest.markGradebookRecovered(); // Duplicate marking
        quest.markPhantomDefeated();
        quest.markPhantomDefeated(); // Duplicate marking

        assertTrue(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertTrue(quest.isComplete());
        assertEquals(QuestStatus.COMPLETED, quest.getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "false, false, NOT_STARTED",  // Both goals incomplete
            "true, false, IN_PROGRESS",  // Only Gradebook recovered
            "false, true, IN_PROGRESS",  // Only Phantom defeated
            "true, true, COMPLETED"      // Both goals completed
    })
    void parametrizedUpdateStatusCoversAllScenarios(boolean gradebook, boolean phantom, QuestStatus expectedStatus) {
        Quest quest = new Quest("Escape", "Recover and defeat.");

        // Mark goals as per scenario
        if (gradebook) {
            quest.markGradebookRecovered();
        }
        if (phantom) {
            quest.markPhantomDefeated();
        }

        assertEquals(expectedStatus, quest.getStatus());
        assertEquals(gradebook, quest.isGradebookRecovered());
        assertEquals(phantom, quest.isPhantomDefeated());
        assertEquals(gradebook && phantom, quest.isComplete());
    }
}
