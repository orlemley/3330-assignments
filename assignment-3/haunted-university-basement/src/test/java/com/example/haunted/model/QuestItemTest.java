package com.example.haunted.model;

public class QuestItemTest extends ItemTest {
    @Override
    protected Item createItem(String name, String description) {
        return new QuestItem(name, description);
    }
}
