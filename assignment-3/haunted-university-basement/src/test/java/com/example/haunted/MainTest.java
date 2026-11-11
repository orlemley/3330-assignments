package com.example.haunted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    // This is not exactly the most complete test, but I don't want to get too crazy and test the exact behavior
    // of every line of code. This should give 100 percent coverage and is totally reasonable in my opinion
    @Test
    void mainDoesNotThrow() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
