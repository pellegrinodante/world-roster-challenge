package com.dante.worldroster.random;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class SeededRandom {
    private final long seed;
    private final Random random;

    public SeededRandom(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public long seed() {
        return seed;
    }

    public double nextDouble(double minimum, double maximum) {
        if (minimum >= maximum) {
            throw new IllegalArgumentException("El mínimo debe ser menor que el máximo");
        }
        return minimum + random.nextDouble() * (maximum - minimum);
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public <T> void shuffle(List<T> values) {
        Collections.shuffle(values, random);
    }
}
