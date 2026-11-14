package com.example.haunted.model;

public class KeyTest extends ItemTest {
    @Override
    protected Item createItem(String name, String description) {
        return new Key(name, description);
    }
}
