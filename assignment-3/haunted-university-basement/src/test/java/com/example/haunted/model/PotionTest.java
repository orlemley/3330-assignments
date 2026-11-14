package com.example.haunted.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PotionTest extends ItemTest {
    @Override
    protected Item createItem(String name, String description) {
        return new Potion(name, description, 15);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 15, Integer.MAX_VALUE})
    void healingAmountReturnsConstructorValue(int amount) {
        Potion potion = new Potion("Coffee", "Potion description.", amount);

        assertEquals(amount, potion.getHealingAmount());
        assertInstanceOf(Usable.class, potion);
    }

    @ParameterizedTest
    @CsvSource({"20, 15, 45", "5, 15, 50", "0, 15, 50", "20, 0, 30",
            "20, -1, 30", "20, -2147483648, 30", "50, 15, 15"})
    void useHealsRealPlayerWithoutExceedingMaximum(int damage, int healing, int expectedHealth) {
        Player player = new Player("Student", 50, 7, 2, new Inventory(8));
        player.takeDamage(damage);
        Potion potion = new Potion("Coffee", "Potion description.", healing);

        potion.use(player);

        assertEquals(expectedHealth, player.getHealth());
        assertEquals(50, player.getMaxHealth());
        assertEquals(healing, potion.getHealingAmount());
        assertEquals("Coffee", potion.getName());
        assertEquals("Potion description.", potion.getDescription());
    }
}
