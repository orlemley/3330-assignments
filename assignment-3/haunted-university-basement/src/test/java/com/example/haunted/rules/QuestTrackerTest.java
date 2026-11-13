package com.example.haunted.rules;

import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class QuestTrackerTest {
    private QuestTracker questTracker;

    @BeforeEach
    void setUp() {
        questTracker = new QuestTracker();
    }

    @Test
    void updateQuestForItem_ShouldMarkGradebookRecovered_WhenCorrectItemProvided() {
        // Arrange
        Quest quest = mock(Quest.class);
        Item item = mock(Item.class);

        when(item.getName()).thenReturn("Lost Gradebook");

        // Act
        questTracker.updateQuestForItem(quest, item);

        // Assert
        verify(quest, times(1)).markGradebookRecovered();
    }

    @Test
    void updateQuestForItem_ShouldDoNothing_WhenIncorrectItemProvided() {
        // Arrange
        Quest quest = mock(Quest.class);
        Item item = mock(Item.class);

        when(item.getName()).thenReturn("Wrong Item");

        // Act
        questTracker.updateQuestForItem(quest, item);

        // Assert
        verify(quest, never()).markGradebookRecovered();
    }

    @Test
    void updateQuestForItem_ShouldDoNothing_WhenItemIsNull() {
        // Arrange
        Quest quest = mock(Quest.class);

        // Act
        questTracker.updateQuestForItem(quest, null);

        // Assert
        verify(quest, never()).markGradebookRecovered();
    }

    @Test
    void updateQuestForMonster_ShouldMarkPhantomDefeated_WhenCorrectMonsterDefeated() {
        // Arrange
        Quest quest = mock(Quest.class);
        Monster monster = mock(Monster.class);

        when(monster.getName()).thenReturn("Final Exam Phantom");
        when(monster.isAlive()).thenReturn(false);

        // Act
        questTracker.updateQuestForMonster(quest, monster);

        // Assert
        verify(quest, times(1)).markPhantomDefeated();
    }

    @Test
    void updateQuestForMonster_ShouldDoNothing_WhenMonsterIsAlive() {
        // Arrange
        Quest quest = mock(Quest.class);
        Monster monster = mock(Monster.class);

        when(monster.getName()).thenReturn("Final Exam Phantom");
        when(monster.isAlive()).thenReturn(true);

        // Act
        questTracker.updateQuestForMonster(quest, monster);

        // Assert
        verify(quest, never()).markPhantomDefeated();
    }

    @Test
    void updateQuestForMonster_ShouldDoNothing_WhenIncorrectMonsterProvided() {
        // Arrange
        Quest quest = mock(Quest.class);
        Monster monster = mock(Monster.class);

        when(monster.getName()).thenReturn("Wrong Monster");
        when(monster.isAlive()).thenReturn(false);

        // Act
        questTracker.updateQuestForMonster(quest, monster);

        // Assert
        verify(quest, never()).markPhantomDefeated();
    }

    @Test
    void updateQuestForMonster_ShouldDoNothing_WhenMonsterIsNull() {
        // Arrange
        Quest quest = mock(Quest.class);

        // Act
        questTracker.updateQuestForMonster(quest, null);

        // Assert
        verify(quest, never()).markPhantomDefeated();
    }
}