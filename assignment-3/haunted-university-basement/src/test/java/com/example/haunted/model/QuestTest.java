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
}
