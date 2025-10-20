package com.nettakrim.spyglass_astronomy;

import java.util.ArrayList;
import java.util.Random;


public class IntTetrisBagRandom {
    public ArrayList<Integer> list;
    public final int max;
    private final Random random;

    public IntTetrisBagRandom(Random random, int max) {
        this.random = random;
        this.max = max;
        reset();
    }

    public void reset() {
        list = new ArrayList<>();
        for (int x = 0; x < max+1; x++) {
            list.add(x);
        }
    }

    public int get() {
        if (list.isEmpty()) reset();
        int pos = random.nextInt(list.size());
        return list.remove(pos);
    }
}
