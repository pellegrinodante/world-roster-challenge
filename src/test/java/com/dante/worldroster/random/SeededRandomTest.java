package com.dante.worldroster.random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeededRandomTest {
    @Test
    void sameSeedProducesSameSequence() {
        SeededRandom first = new SeededRandom(12345L);
        SeededRandom second = new SeededRandom(12345L);
        for (int index = 0; index < 20; index++) {
            assertEquals(first.nextDouble(-10, 10), second.nextDouble(-10, 10));
        }
    }
}
