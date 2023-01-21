package com.rafaskoberg.gdx.parrot.util;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class helps control the randomness of the shuffling of sounds. If you play the same sound type multiple times, this class can help
 * prevent the same sound type index from playing twice in a row, makes a sound type index more likely to play if it hasn't for a while,
 * etc.
 */
public class NonRandomShuffle<T> {
    private List<WeightPair<T>> values = new ArrayList<>();
    private float dropOffMultiplier;

    /**
     * @param dropOffMultiplier The closer to 0 this is, the more uniform the weights will be. The closer to 1, the more likely something is
     *                          to roll if it hasn't rolled in a while, and vice versa.
     */
    public NonRandomShuffle(float dropOffMultiplier) {
        if(dropOffMultiplier < 0 || dropOffMultiplier > 1)
            throw new IllegalArgumentException("dropOffMultiplier must be in the 0-1 range.");

        this.dropOffMultiplier = dropOffMultiplier;
    }

    public void addValue(T object) {
        values.add(new WeightPair<>(object));
    }

    public List<WeightPair<T>> getValues() {
        return values;
    }

    public void setValues(T... objects) {
        values.clear();
        for(T object : objects) {
            values.add(new WeightPair<>(object));
        }
    }

    public void clearValues() {
        values.clear();
    }

    /** Returns a random element from this list according to the provided dropOffMultiplier. */
    public T get() {
        // Empty list
        if(values.isEmpty()) {
            return null;
        }

        // Calculate weight
        int totalWeight = 0;
        for(int i = 0; i < values.size(); i++) {
            totalWeight += values.get(i).weight;
        }

        // Get random weighted index
        int idx = 0;
        for(float r = MathUtils.random(0f, 1f) * totalWeight; idx < values.size() - 1; ++idx) {
            r -= values.get(idx).weight;
            if(r <= 0.0) {
                break;
            }
        }

        // Reassign weights to make the chosen element less likely to be picked again, and all others to be more likely
        for(int i = 0; i < values.size(); i++) {
            if(i == idx) {
                values.get(i).weight *= (1 - dropOffMultiplier);
            } else {
                values.get(i).weight += dropOffMultiplier;
            }
        }

        // Return object
        return values.get(idx).object;
    }

    public static class WeightPair<T> {
        float weight;
        T object;

        WeightPair(T object) {
            this.weight = 1;
            this.object = object;
        }
    }

}
