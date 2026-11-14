package com.example.haunted.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ItemTest {
    protected abstract Item createItem(String name, String description);

    @Test
    void constructorPassesNameAndDescriptionToItem() {
        Item item = createItem("Test Item", "A distinct description.");

        assertEquals("Test Item", item.getName());
        assertEquals("A distinct description.", item.getDescription());
    }

    @Test
    void toStringReturnsName() {
        Item item = createItem("Test Item", "A distinct description.");

        assertEquals("Test Item", item.toString());
    }

    @Test
    void emptyStringsArePreserved() {
        Item item = createItem("", "");

        assertEquals("", item.getName());
        assertEquals("", item.getDescription());
        assertEquals("", item.toString());
    }

    @Test
    void nullNameAndDescriptionAreRejectedSeparately() {
        assertThrows(NullPointerException.class, () -> createItem(null, "Description."));
        assertThrows(NullPointerException.class, () -> createItem("Name", null));
    }
}
