package com.example.haunted.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsableTest {
    @Test
    void useCanBeCalledThroughInterfaceOnRealPotion() {
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        player.takeDamage(20);
        Usable usable = new Potion("Coffee", "Heals fifteen health.", 15);

        usable.use(player);

        assertEquals(45, player.getHealth());
    }
}
