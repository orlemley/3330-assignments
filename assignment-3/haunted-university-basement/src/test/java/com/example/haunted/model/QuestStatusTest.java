package com.example.haunted.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class QuestStatusTest {
    @Test
    void valuesContainExactlyDeclaredConstantsInOrder() {
        QuestStatus[] expected = {QuestStatus.NOT_STARTED, QuestStatus.IN_PROGRESS, QuestStatus.COMPLETED};

        assertArrayEquals(expected, QuestStatus.values());
        QuestStatus[] copy = QuestStatus.values();
        copy[0] = null;
        assertArrayEquals(expected, QuestStatus.values());
    }

    @ParameterizedTest
    @EnumSource(QuestStatus.class)
    void valueOfReturnsSameConstantForExactName(QuestStatus value) {
        assertSame(value, QuestStatus.valueOf(value.name()));
        assertEquals(value.name(), value.toString());
    }

    @Test
    void valueOfRejectsInvalidNamesAndNull() {
        assertThrows(IllegalArgumentException.class, () -> QuestStatus.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> QuestStatus.valueOf("completed"));
        assertThrows(IllegalArgumentException.class, () -> QuestStatus.valueOf(""));
        assertThrows(NullPointerException.class, () -> QuestStatus.valueOf(null));
    }
}
