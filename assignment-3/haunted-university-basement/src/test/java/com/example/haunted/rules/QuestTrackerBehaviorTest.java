package com.example.haunted.rules;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestTrackerBehaviorTest {
    @ParameterizedTest
    @CsvSource({"Lost Gradebook, true", "lOsT gRaDeBoOk, true", "Wrong Item, false"})
    void itemNamesUpdateOnlyGradebookGoal(String name, boolean expected) {
        Quest quest = new Quest("Escape", "Complete both goals.");
        Item item = new QuestItem(name, "Test item.");

        new QuestTracker().updateQuestForItem(quest, item);

        assertEquals(expected, quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertEquals(expected ? QuestStatus.IN_PROGRESS : QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isComplete());
    }

    @ParameterizedTest
    @CsvSource({"Final Exam Phantom, 0, true", "fInAl eXaM pHaNtOm, 0, true",
            "Final Exam Phantom, 1, false", "Other, 0, false", "Other, 1, false"})
    void monsterMustHaveCorrectNameAndBeDefeated(String name, int health, boolean expected) {
        Quest quest = new Quest("Escape", "Complete both goals.");
        Monster monster = new Monster(name, health, 7, 2, List.of());

        new QuestTracker().updateQuestForMonster(quest, monster);

        assertEquals(expected, quest.isPhantomDefeated());
        assertFalse(quest.isGradebookRecovered());
        assertEquals(expected ? QuestStatus.IN_PROGRESS : QuestStatus.NOT_STARTED, quest.getStatus());
        assertFalse(quest.isComplete());
    }

    @Test
    void realQuestCompletesAndNullInputsCannotChangeItsGoals() {
        Quest quest = new Quest("Escape", "Complete both goals.");
        QuestTracker tracker = new QuestTracker();
        tracker.updateQuestForItem(quest, null);
        tracker.updateQuestForMonster(quest, null);
        assertFalse(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
        assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
        tracker.updateQuestForItem(quest, new QuestItem("Lost Gradebook", "Goal item."));
        tracker.updateQuestForMonster(quest, new Monster("Final Exam Phantom", 0, 7, 2, List.of()));
        assertTrue(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertTrue(quest.isComplete());
    }
}
